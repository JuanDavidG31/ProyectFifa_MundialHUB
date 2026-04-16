import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { Subscription } from 'rxjs';
interface AuditEvent {
  date: Date;
  description: string;
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS';
}

interface SupportCase {
  id: string;
  username: string;
  issueType: string;
  status: 'PENDIENTE' | 'EN_REVISION' | 'RESUELTO';
  createdAt: Date;
  timeline: AuditEvent[];
}

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink], 
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.scss']
})
export class SupportComponent implements OnInit {
  
  // NUEVO: Variables para controlar la ventana del chat
  isChatOpen = false;
  myUsername = localStorage.getItem('username') || 'Agente';
  cases: SupportCase[] = [
    {
      id: 'CASO-001',
      username: 'juanperez99',
      issueType: 'No me llegó la confirmación',
      status: 'PENDIENTE',
      createdAt: new Date(),
      timeline: [
        { date: new Date(Date.now() - 3600000), description: 'Usuario intentó compra de entrada', type: 'INFO' },
        { date: new Date(Date.now() - 3500000), description: 'Pago simulado procesado correctamente', type: 'SUCCESS' },
        { date: new Date(Date.now() - 3400000), description: 'Fallo al enviar email de confirmación', type: 'ERROR' }
      ]
    },
    {
      id: 'CASO-002',
      username: 'mariagomez',
      issueType: 'Mi entrada expiró',
      status: 'EN_REVISION',
      createdAt: new Date(Date.now() - 86400000),
      timeline: [
        { date: new Date(Date.now() - 90000000), description: 'Reserva creada (TTL 15 min)', type: 'INFO' },
        { date: new Date(Date.now() - 89100000), description: 'Reserva expirada por falta de pago', type: 'WARNING' }
      ]
    }
  ];

  selectedCase: SupportCase | null = null;
  isLoadingAction = false;
  chatMessages: {sender: string, text: string, isMe: boolean}[] = [];
  newMessage = '';
  chatStatus = 'Esperando a que un usuario solicite ayuda...';
  chatSubscription!: Subscription;

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.chatService.connect(); // Esto dispara el "agentAvailable" por ser SUPPORT
    
    this.chatSubscription = this.chatService.messages$.subscribe(msg => {
      if (!msg) return;

      if (msg.type === 'ASSIGN') {
        this.chatStatus = `¡Chat asignado! Hablando con usuario: ${msg.content}`;
        this.chatMessages.push({ sender: 'Sistema', text: `Usuario ${msg.content} asignado.`, isMe: false });
      } else if (msg.type === 'CHAT') {
        this.chatMessages.push({ sender: msg.sender, text: msg.content, isMe: false });
      } else if (msg.type === 'DISCONNECT') {
        this.chatStatus = 'El usuario se desconectó. Vuelves a estar disponible.';
        this.chatMessages.push({ sender: 'Sistema', text: 'Fin del chat.', isMe: false });
      }
    });
  }
  toggleChat() {
    this.isChatOpen = !this.isChatOpen;
  }
  ngOnDestroy(): void {
    this.chatSubscription?.unsubscribe();
    this.chatService.disconnect();
  }
  sendChatMessage() {
    if (this.newMessage.trim() === '') return;
    this.chatMessages.push({ sender: this.myUsername, text: this.newMessage, isMe: true });
    this.chatService.sendMessage(this.myUsername, this.newMessage);
    this.newMessage = '';
  }

  closeChatSession() {
    this.chatService.closeSession(this.myUsername, 'AGENT');
    this.chatStatus = 'Esperando a que un usuario solicite ayuda...';
    this.chatMessages = [];
  }

  selectCase(supportCase: SupportCase): void {
    this.selectedCase = supportCase;
  }

  // Acciones predefinidas según el documento
  executeAction(action: 'REINTENTAR_NOTIFICACION' | 'REACTIVAR_RESERVA' | 'ABRIR_INVESTIGACION'): void {
    if (!this.selectedCase) return;
    
    this.isLoadingAction = true;
    // Aquí iría la llamada a tu servicio (ej. this.supportService.executeAction(...))
    setTimeout(() => {
      let actionDesc = '';
      switch (action) {
        case 'REINTENTAR_NOTIFICACION': actionDesc = 'Notificación reenviada manualmente por soporte.'; break;
        case 'REACTIVAR_RESERVA': actionDesc = 'Reserva reactivada. Nuevo TTL asignado.'; break;
        case 'ABRIR_INVESTIGACION': actionDesc = 'Caso escalado a Compliance / Investigación.'; break;
      }

      // Agregamos el evento a la línea de tiempo simulando la respuesta del back
      this.selectedCase?.timeline.unshift({
        date: new Date(),
        description: actionDesc,
        type: 'SUCCESS'
      });
      
      if(action === 'ABRIR_INVESTIGACION' && this.selectedCase) this.selectedCase.status = 'EN_REVISION';
      
      this.isLoadingAction = false;
      alert(`Acción "${action}" ejecutada con éxito.`);
    }, 1000);
  }
}