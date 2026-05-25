import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { AlbumService } from '../../features/album/services/album.service';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ViewChild, ElementRef } from '@angular/core';
import { FootballService } from '../../core/services/football.service';
import { UserService } from '../../core/services/user.service';
import { NoticeService } from '../../core/services/notice.service';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent implements OnInit {
  cardName: string = '';
  cardNumber: string = '';
  cardExpiry: string = '';
  cardCvc: string = '';
  userCoins: number = 0;
  isRechargeModalOpen = false;
  rechargeAmount: number | null = null;
  isProcessingPayment = false;
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
  noticias: any[] = [];
  showTutorial = false;
  currentTutorialStep = 0;
  favoriteTeam: string = 'Cargando...';
  tutorialSteps = [
    {
      icon: '',
      title: '¡Bienvenido a MundialHub!',
      desc: 'Tu plataforma definitiva para vivir la pasión del fútbol. Te daremos un recorrido rápido por todo lo que puedes hacer.'
    },
    {
      icon: '',
      title: 'Compra de Tickets',
      desc: 'Encuentra y asegura tus entradas para los mejores partidos con un código QR oficial.'
    },
    {
      icon: '',
      title: 'Tu Álbum Virtual',
      desc: 'Colecciona, abre sobres e intercambia cromos con otros usuarios para completar tu equipo ideal.'
    },
    {
      icon: '',
      title: 'Pronósticos y Apuestas',
      desc: 'Demuestra cuánto sabes de fútbol, adivina los resultados y compite en el ranking global.'
    },
    {
      icon: '',
      title: 'Equipos y Estadísticas',
      desc: 'Sigue el rendimiento de tu selección favorita y mantente al día con el torneo.'
    }
  ];

  constructor(
    private albumService: AlbumService,
    private authService: AuthService,
    private router: Router,
    private chatService: ChatService,
    private footballService: FootballService,
    private userService: UserService,
    private noticeService: NoticeService
  ) { }

  ngOnInit(): void {

    this.loadUserData();
    if (localStorage.getItem('verify') !== 'true') {
      alert('Acceso denegado. Debes completar la verificación de seguridad.');
      this.authService.logout();
      return;
    }
    window.scrollTo(0, 0);
    const role = this.authService.getRole();
    this.isAdmin = role === 'ADMIN';
    this.isSupport = role === 'SUPPORT';

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
    this.cargarNoticias();
  }

  formatCardNumber(event: any) {
    let input = event.target.value.replace(/\D/g, '').substring(0, 16);
    input = input != '' ? input.match(/.{1,4}/g).join(' ') : '';
    this.cardNumber = input;
  }

  formatExpiry(event: any) {
    let input = event.target.value.replace(/\D/g, '').substring(0, 4);
    if (input.length > 2) {
      this.cardExpiry = input.substring(0, 2) + '/' + input.substring(2, 4);
    } else {
      this.cardExpiry = input;
    }
  }

  formatCvc(event: any) {
    this.cardCvc = event.target.value.replace(/\D/g, '').substring(0, 3);
  }

  cargarNoticias() {
    this.noticeService.getNotices().subscribe({
      next: (data) => {
        this.noticias = data;
      },
      error: (err) => console.error('Error cargando noticias:', err)
    });
  }

  toggleAvatarModal() {
    if (this.userAvatar) {
      this.isAvatarModalOpen = !this.isAvatarModalOpen;
    }
  }

  onScrollRangeChange(event: any) {
    const scrollVal = event.target.value;
    const el = this.carousel.nativeElement;
    const maxScroll = el.scrollWidth - el.clientWidth;
    el.scrollLeft = (scrollVal / 100) * maxScroll;
    this.scrollProgress = scrollVal;
  }

  onCarouselScroll(event: any) {
    const el = event.target;
    const maxScroll = el.scrollWidth - el.clientWidth;
    if (maxScroll > 0) {
      this.scrollProgress = (el.scrollLeft / maxScroll) * 100;
    }
  }


  loadFavoriteTeam(): void {
    const username = localStorage.getItem('userName') || 'normaluser';
    this.footballService.getUserDashboard(username).subscribe({
      next: (data) => {
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
    this.chatStatus = 'Verificando disponibilidad de agentes...';

    this.userService.checkActiveSupport().subscribe({
      next: (hasSupport: boolean) => {
        if (hasSupport) {
          this.chatMessages = [];
          this.chatStatus = 'Buscando agente...';
          this.chatService.requestSupport(this.userName);
        } else {
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

    this.chatMessages.push({ sender: this.userName, text: this.newMessage, isMe: true });
    this.chatService.sendMessage(this.userName, this.newMessage);
    this.newMessage = '';
  }

  closeChatSession() {
    this.chatService.closeSession(this.userName, 'USER');
    this.chatStatus = 'Inicia un chat con soporte';
    this.chatMessages = [];
  }

  cerrarSesion(): void {
    this.updateStatusConnectFalse();

    this.verificarCierreSeguro();
  }

  verificarCierreSeguro(): void {
    if (localStorage.getItem('countActive') === 'false') {

      this.albumService.clearAlbumState();
      localStorage.removeItem('userAvatar');
      this.authService.logout();

    } else {
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
      this.userService.updateStatusConnectTrue(this.userId).subscribe({
        next: () => {
          localStorage.setItem('countActive', 'true');
          console.log('Connect status updated on server');

        }
      });
    }
  }

  updateStatusConnectFalse() {

    if (this.userId !== null) {
      this.userService.updateStatusConnectFalse(this.userId).subscribe({
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
      this.userService.updateTutorialStatus(this.userId).subscribe({
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
      this.userService.getUserByUsername(currentUsername).subscribe({
        next: (me) => {
          if (me) {
            this.userCoins = me.coins || 0;

            this.userId = me.id;
          }
        },
        error: () => console.error('Error cargando los datos del usuario')
      });
    }
  }

  openRechargeModal() {
    this.isRechargeModalOpen = true;
    this.rechargeAmount = null;
  }

  closeRechargeModal() {
    this.isRechargeModalOpen = false;
  }

  processPayment() {
    if (!this.rechargeAmount || this.rechargeAmount <= 0) {
      alert("Por favor, ingresa un monto válido.");
      return;
    }

    this.isProcessingPayment = true;
    const currentUsername = localStorage.getItem('username') || '';

    setTimeout(() => {
      this.userService.rechargeUserCoins(currentUsername, this.rechargeAmount!).subscribe({
        next: (newBalance) => {
          this.userCoins = newBalance;
          this.isProcessingPayment = false;
          this.closeRechargeModal();
          alert(`¡Pago Exitoso! Has recargado ${this.rechargeAmount} $ a tu cuenta.`);
        },
        error: (err) => {
          console.error("Error en la recarga", err);
          this.isProcessingPayment = false;
          alert("Hubo un error procesando tu pago con el banco.");
        }
      });
    }, 2000);
  }
}