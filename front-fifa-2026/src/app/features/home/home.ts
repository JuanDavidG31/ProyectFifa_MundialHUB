import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { AlbumService } from '../../features/album/services/album.service';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ViewChild, ElementRef } from '@angular/core';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent implements OnInit {
  isAvatarModalOpen = false;
  @ViewChild('carousel') carousel!: ElementRef;
  scrollProgress: number = 0;
  userName: string = 'Usuario';
  isAdmin: boolean = false;
  isSupport: boolean = false;
  userAvatar: string | null = null;
  isChatOpen = false;
  chatMessages: { sender: string, text: string, isMe: boolean }[] = [];
  newMessage = '';
  chatStatus = 'Inicia un chat con soporte';
  chatSubscription!: Subscription;
  userId: number | null = null;

  showTutorial = false;
  currentTutorialStep = 0;
  favoriteTeam: string = 'Cargando...';
  tutorialSteps = [
    {
      icon: '👋',
      title: '¡Bienvenido a MundialHub!',
      desc: 'Tu plataforma definitiva para vivir la pasión del fútbol. Te daremos un recorrido rápido por todo lo que puedes hacer.'
    },
    {
      icon: '🎟️',
      title: 'Compra de Tickets',
      desc: 'Encuentra y asegura tus entradas para los mejores partidos con un código QR oficial.'
    },
    {
      icon: '📒',
      title: 'Tu Álbum Virtual',
      desc: 'Colecciona, abre sobres e intercambia cromos con otros usuarios para completar tu equipo ideal.'
    },
    {
      icon: '🎯',
      title: 'Pronósticos y Apuestas',
      desc: 'Demuestra cuánto sabes de fútbol, adivina los resultados y compite en el ranking global.'
    },
    {
      icon: '🌍',
      title: 'Equipos y Estadísticas',
      desc: 'Sigue el rendimiento de tu selección favorita y mantente al día con el torneo.'
    }
  ];

  constructor(
    private albumService: AlbumService,
    private authService: AuthService,
    private router: Router,
    private chatService: ChatService, // <-- INYECTAR
  ) { }

  ngOnInit(): void {

    this.loadUserData();
    window.scrollTo(0, 0);
    const role = this.authService.getRole();
    this.isAdmin = role === 'ADMIN';
    this.isSupport = role === 'SUPPORT';

    // Cargar nombre
    const nombreLimpio = localStorage.getItem('userName');
    if (nombreLimpio) {
      this.userName = nombreLimpio;
    } else {
      const decoded = this.authService.getDecodedToken();
      if (decoded && decoded.sub) {
        this.userName = decoded.sub;
      } else {
        this.userName = 'Usuario';
      }
    }

    this.userAvatar = localStorage.getItem('userAvatar');

    this.checkTutorialStatus();

    this.chatService.connect();

    // Escuchar mensajes
    this.chatSubscription = this.chatService.messages$.subscribe(msg => {
      if (!msg) return;

      if (msg.type === 'WAITING') {
        this.chatStatus = msg.content;
      } else if (msg.type === 'ASSIGN') {
        this.chatStatus = `Conectado con agente: ${msg.content}`;
        this.chatMessages.push({ sender: 'Sistema', text: `Agente ${msg.content} se ha unido al chat.`, isMe: false });
      } else if (msg.type === 'CHAT') {
        this.chatMessages.push({ sender: msg.sender, text: msg.content, isMe: false });
      } else if (msg.type === 'DISCONNECT') {
        this.chatStatus = 'Chat finalizado';
        this.chatMessages.push({ sender: 'Sistema', text: 'El agente cerró la conexión.', isMe: false });
      }
    });
    this.loadFavoriteTeam();
  }

  toggleAvatarModal() {
  if (this.userAvatar) { // Solo abrimos si realmente hay una imagen
    this.isAvatarModalOpen = !this.isAvatarModalOpen;
  }
}

  // Mueve el carrusel cuando el usuario arrastra la barra superior
  onScrollRangeChange(event: any) {
    const scrollVal = event.target.value;
    const el = this.carousel.nativeElement;
    const maxScroll = el.scrollWidth - el.clientWidth;
    el.scrollLeft = (scrollVal / 100) * maxScroll;
  }

  // Actualiza la barra superior si el usuario desliza con el dedo (táctil)
  onCarouselScroll(event: any) {
    const el = event.target;
    const maxScroll = el.scrollWidth - el.clientWidth;
    if (maxScroll > 0) {
      this.scrollProgress = (el.scrollLeft / maxScroll) * 100;
    }
  }


  loadFavoriteTeam(): void {
    const username = localStorage.getItem('userName') || 'normaluser';
    this.authService.getUserDashboard(username).subscribe({
      next: (data) => {
        // Igual que en TeamsComponent, usamos userCountry
        this.favoriteTeam = data.userCountry || 'Ninguno';
      },
      error: (err) => {
        console.error('Error cargando el equipo favorito', err);
        this.favoriteTeam = 'Desconocido';
      }
    });
  }

  updateStatusConnect() {
    this.updateStatusConnectTrue();
  }

  ngOnDestroy(): void {
    this.chatSubscription?.unsubscribe();
    this.chatService.disconnect();
  }

  toggleChat() {
    this.isChatOpen = !this.isChatOpen;
  }

  requestAgent() {
    // 1. Ponemos un estado de espera mientras le preguntamos al backend
    this.chatStatus = 'Verificando disponibilidad de agentes...';

    // 2. Llamamos a nuestro nuevo endpoint
    this.authService.checkActiveSupport().subscribe({
      next: (hasSupport: boolean) => {
        if (hasSupport) {
          // ✅ SÍ HAY SOPORTE: Procedemos a conectarnos al WebSocket
          this.chatMessages = [];
          this.chatStatus = 'Buscando agente...';
          this.chatService.requestSupport(this.userName);
        } else {
          // ❌ NO HAY SOPORTE: Bloqueamos y avisamos al usuario
          this.chatStatus = 'No hay agentes de soporte conectados en este momento.';
          this.chatMessages = [];
        }
      },
      error: (err) => {
        console.error("Error al verificar soporte", err);
        this.chatStatus = 'Error al verificar disponibilidad. Intenta de nuevo.';
      }
    });
  }

  sendChatMessage() {
    if (this.newMessage.trim() === '') return;

    // Lo mostramos localmente
    this.chatMessages.push({ sender: this.userName, text: this.newMessage, isMe: true });
    // Lo enviamos por WS
    this.chatService.sendMessage(this.userName, this.newMessage);
    this.newMessage = '';
  }

  closeChatSession() {
    this.chatService.closeSession(this.userName, 'USER');
    this.chatStatus = 'Inicia un chat con soporte';
    this.chatMessages = [];
  }

  cerrarSesion(): void {
    // 1. Disparamos la orden al servidor UNA SOLA VEZ
    this.updateStatusConnectFalse();

    // 2. Iniciamos el ciclo de verificación
    this.verificarCierreSeguro();
  }

  verificarCierreSeguro(): void {
    // Revisamos si el servidor ya respondió y la variable cambió
    if (localStorage.getItem('countActive') === 'false') {

      // ¡Éxito! Limpiamos todo y cerramos sesión
      this.albumService.clearAlbumState();
      localStorage.removeItem('userAvatar');
      this.authService.logout();

    } else {
      // Si todavía no ha cambiado, esperamos 200 milisegundos y nos volvemos a llamar a nosotros mismos
      setTimeout(() => {
        this.verificarCierreSeguro();
      }, 200);
    }
  }


  checkTutorialStatus() {
    const tutorialVisto = localStorage.getItem('tutorialView');
    if (tutorialVisto === 'false') {
      setTimeout(() => {
        this.showTutorial = true;
      }, 500);
    }
  }

  nextTutorialStep() {
    if (this.currentTutorialStep < 5) { 
      this.currentTutorialStep++;
    } else {
      this.finishTutorial();
    }
  }

  prevTutorialStep() {
    if (this.currentTutorialStep > 0) {
      this.currentTutorialStep--;
    }
  }

  updateStatusConnectTrue() {

    if (this.userId !== null) {
      this.authService.updateStatusConnectTrue(this.userId).subscribe({
        next: () => {
          localStorage.setItem('countActive', 'true');
          console.log('Connect status updated on server');

        }
      });
    }
  }

  updateStatusConnectFalse() {

    if (this.userId !== null) {
      this.authService.updateStatusConnectFalse(this.userId).subscribe({
        next: () => {
          localStorage.setItem('countActive', 'false');
          console.log('Connect status updated on server');

        }
      });
    }
  }

  finishTutorial() {
    this.showTutorial = false;

    if (this.userId !== null) {
      this.authService.updateTutorialStatus(this.userId).subscribe({
        next: () => {
          console.log('Tutorial status updated on server');
        }
      });
    }
    localStorage.setItem('tutorialView', 'true');
  }

  loadUserData(): void {
    const currentUsername = localStorage.getItem('username');
    if (currentUsername) {
      this.authService.getUserByUsername(currentUsername).subscribe({
        next: (me) => {
          if (me) {
            this.userId = me.id;
          }
        },
        error: () => console.error('Error cargando los datos del usuario')
      });
    }
  }
}