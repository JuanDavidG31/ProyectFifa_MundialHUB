import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface ChatMessage {
  type: 'CHAT' | 'ASSIGN' | 'DISCONNECT' | 'WAITING' | 'SYSTEM';
  sender: string;
  recipient: string;
  content: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private stompClient: Client | null = null;
  private messageSubject = new BehaviorSubject<ChatMessage | null>(null);
  public messages$ = this.messageSubject.asObservable();
  
  private currentPeer: string = '';

  constructor(private authService: AuthService) {}

  connect(): void {
    const token = this.authService.getToken();
    if (!token) return;

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}` // Tu backend pide el token aquí
      },
      debug: (str) => { console.log(str); },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = (frame) => {
      console.log('✅ Conectado al WS');
      const username = localStorage.getItem('username'); // Obtenemos el usuario actual
      
      // Suscribirse a la cola privada
      this.stompClient?.subscribe(`/queue/chat/${username}`, (message: Message) => {
        const payload = JSON.parse(message.body) as ChatMessage;
        
        if (payload.type === 'ASSIGN') {
          this.currentPeer = payload.content; // El server manda el nombre de la otra persona en el content
        } else if (payload.type === 'DISCONNECT') {
          this.currentPeer = '';
        }

        this.messageSubject.next(payload);
      });

      // Si es rol de soporte, avisar inmediatamente que está disponible
      if (this.authService.getRole() === 'SUPPORT' && username) {
        this.stompClient?.publish({
          destination: '/app/chat.agentAvailable',
          body: JSON.stringify({ type: 'SYSTEM', sender: username, recipient: 'SERVER', content: '' })
        });
      }
    };

    this.stompClient.activate();
  }

  // Llamado por el usuario en Home
  requestSupport(username: string): void {
    this.stompClient?.publish({
      destination: '/app/chat.requestSupport',
      body: JSON.stringify({ type: 'SYSTEM', sender: username, recipient: 'SERVER', content: '' })
    });
  }

  // Enviar mensaje de texto
  sendMessage(sender: string, content: string): void {
    if (!this.currentPeer) return;
    const msg: ChatMessage = { type: 'CHAT', sender, recipient: this.currentPeer, content };
    
    this.stompClient?.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(msg)
    });
  }

  // Cerrar el chat
  closeSession(sender: string, role: string): void {
    this.stompClient?.publish({
      destination: '/app/chat.closeSession',
      body: JSON.stringify({ type: 'DISCONNECT', sender, recipient: 'SERVER', content: role })
    });
    this.currentPeer = '';
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }

  getCurrentPeer(): string {
    return this.currentPeer;
  }
}