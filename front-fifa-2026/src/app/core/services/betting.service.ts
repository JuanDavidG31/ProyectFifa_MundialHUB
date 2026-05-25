import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from './auth.service';

export interface BetMessageDTO {
  roomId?: string;
  sender: string;
  type: 'JOIN_REQUEST' | 'JOIN_ACCEPT' | 'JOIN_DENY' | 'PREDICTION' | 'ROOM_UPDATE' | 'ROOM_CLOSED';
  content: string;
  targetUser?: string;
  matchInfo?: any;
}

@Injectable({
  providedIn: 'root'
})
export class BettingService {
  private apiUrl = 'https://proyectfifa-mundialhub.onrender.com/betting-rooms';
  private stompClient: Client | null = null;

  public currentRoom$ = new BehaviorSubject<any>(null);
  public joinRequests$ = new BehaviorSubject<BetMessageDTO[]>([]);
  public notifications$ = new Subject<string>(); 

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  getAvailableRooms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/list`);
  }

  recoverRoom(username: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/my-room?username=${username}`);
  }

  forceLeaveRest(username: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/force-leave?username=${username}`);
  }

  createRoom(roomName: string, owner: string): Observable<any> {
    const params = { roomName, owner };
    return this.http.post(`${this.apiUrl}/create`, null, { params });
  }

  connect() {
    if (this.stompClient && this.stompClient.connected) {
      return;
    }

    const username = localStorage.getItem('username') || '';
    const token = this.authService.getToken();

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://proyectfifa-mundialhub.onrender.com/ws'),
      connectHeaders: { Authorization: `Bearer ${token}` },
      onConnect: () => {

        this.stompClient?.subscribe(`/queue/betting/${username}`, (msg: Message) => {
          const body = JSON.parse(msg.body);

          if (body.type === 'JOIN_REQUEST') {
            const currentReqs = this.joinRequests$.value;
            this.joinRequests$.next([...currentReqs, body]);
          } else if (body.type === 'JOIN_DENY') {
            this.notifications$.next('Tu solicitud para unirte fue denegada.');
            this.currentRoom$.next(null);

          } else if (body.type === 'ROOM_CLOSED') {
            this.notifications$.next('La sala ha sido cerrada por el administrador.');
            this.currentRoom$.next(null);
            this.joinRequests$.next([]); // <-- Evita el bug de las solicitudes viejas

          } else {
            this.currentRoom$.next(body);
          }
        });
      }
    });
    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.currentRoom$.next(null);
      this.joinRequests$.next([]);
    }
  }

  sendJoinRequest(roomId: string, myUsername: string) {
    this.sendMessage({
      roomId: roomId,
      sender: myUsername,
      type: 'JOIN_REQUEST',
      content: 'Quiero unirme a la sala'
    });
  }

  respondToRequest(roomId: string, owner: string, targetUser: string, accept: boolean) {
    this.sendMessage({
      roomId: roomId,
      sender: owner,
      targetUser: targetUser,
      type: accept ? 'JOIN_ACCEPT' : 'JOIN_DENY',
      content: accept ? 'Aceptado' : 'Denegado'
    });

    const updatedReqs = this.joinRequests$.value.filter(r => r.sender !== targetUser);
    this.joinRequests$.next(updatedReqs);
  }

  sharePrediction(roomId: string, sender: string, matchData: any) {
    this.sendMessage({
      roomId: roomId,
      sender: sender,
      type: 'PREDICTION',
      content: 'He compartido un pronóstico',
      matchInfo: matchData
    });
  }

  leaveRoom(roomId: string, username: string) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: '/app/betting.leave', 
        body: JSON.stringify({
          roomId: roomId,
          sender: username,
          type: 'LEAVE',
          content: 'Saliendo...'
        })
      });
    }
    this.currentRoom$.next(null);
    this.joinRequests$.next([]); 
  }

  private sendMessage(message: BetMessageDTO) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: '/app/betting.action',
        body: JSON.stringify(message)
      });
    }
  }
}