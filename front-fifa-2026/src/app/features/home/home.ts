import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { AlbumService } from '../../features/album/services/album.service';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], 
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent implements OnInit {
  userName: string = 'Usuario';
  isAdmin: boolean = false;
  isSupport: boolean = false;
  userAvatar: string | null = null; 
  isChatOpen = false;
  chatMessages: {sender: string, text: string, isMe: boolean}[] = [];
  newMessage = '';
  chatStatus = 'Inicia un chat con soporte';
  chatSubscription!: Subscription;
  userId: number | null = null;

  showTutorial = false;
  currentTutorialStep = 0;
  
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
  }

  ngOnDestroy(): void {
    this.chatSubscription?.unsubscribe();
    this.chatService.disconnect();
  }

  toggleChat() {
    this.isChatOpen = !this.isChatOpen;
  }

  requestAgent() {
    this.chatMessages = [];
    this.chatStatus = 'Buscando agente...';
    this.chatService.requestSupport(this.userName);
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
    this.albumService.clearAlbumState();
    localStorage.removeItem('userAvatar'); 
    this.authService.logout();
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
    if (this.currentTutorialStep < this.tutorialSteps.length - 1) {
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

  finishTutorial() {
    this.showTutorial = false;
    
    console.log(this.userId);
    if (this.userId !== null) {
      this.authService.updateTutorialStatus(this.userId).subscribe({
        next: () => {
          console.log('Tutorial status updated on server');
        }
      });
    }
    localStorage.setItem('tutorialView','true');
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