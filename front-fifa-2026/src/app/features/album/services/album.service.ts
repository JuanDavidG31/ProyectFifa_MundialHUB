import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, lastValueFrom } from 'rxjs';
import { AlbumPage, AlbumStatus, Transaction } from '../models/album.models';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
@Injectable({ providedIn: 'root' })
export class AlbumService {
    private apiUrl = 'http://localhost:8080/album';

    private pagesSubject = new BehaviorSubject<AlbumPage[]>([]);
    public pages$ = this.pagesSubject.asObservable();

    // NUEVO: Estado global de economía
    private statusSubject = new BehaviorSubject<AlbumStatus>({ availablePacks: 0, coins: 0 });
    public status$ = this.statusSubject.asObservable();
    private stompClient: Client | null = null;
    public p2pRoom$ = new BehaviorSubject<any>(null);
    constructor(private http: HttpClient) { }
    connectToP2PWebSocket() {
        const token = localStorage.getItem('authToken'); // O usa tu AuthService para obtener el token

        // 1. Creamos el socket apuntando a la ruta de Spring Boot
        const socket = new SockJS('http://localhost:8080/ws');

        // 2. Configuramos el cliente STOMP
        this.stompClient = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}` // Enviamos el JWT para pasar el filtro de seguridad
            },
            reconnectDelay: 5000, // Intenta reconectar si se cae la red
            onConnect: (frame) => {
                console.log('🔗 Conectado a la Sala P2P (WebSocket)');

                // 3. Nos suscribimos al canal personal de este usuario
                this.stompClient?.subscribe('/user/queue/exchange', (message) => {
                    
                    // ======================================================
                    // ¡AQUÍ VA EL NUEVO CÓDIGO! (El Radar)
                    console.log('📥 ¡Mágia WebSocket recibida!', message.body);
                    // ======================================================

                    if (message.body) {
                        const roomState = JSON.parse(message.body);
                        this.p2pRoom$.next(roomState); // ¡Actualiza el frontend al instante!
                    }
                });
                
            },
            onStompError: (frame) => {
                console.error('❌ Error de Broker: ' + frame.headers['message']);
            }
        });

        this.stompClient.activate();
    }

    // Cierra el túnel cuando el usuario sale de la página
    disconnectP2PWebSocket() {
        if (this.stompClient) {
            this.stompClient.deactivate();
            this.p2pRoom$.next(null);
            console.log('🔌 Desconectado de la Sala P2P');
        }
    }
    async inviteP2P(targetUsername: string): Promise<any> {
        return await lastValueFrom(this.http.post<any>(`${this.apiUrl}/p2p/invite?requester=${this.getUsername()}&target=${targetUsername}`, {}, { headers: this.getHeaders() }));
    }

    async pollP2PRoom(): Promise<any> {
        return await lastValueFrom(this.http.get<any>(`${this.apiUrl}/p2p/poll?username=${this.getUsername()}`, { headers: this.getHeaders() }));
    }

    async actionP2P(action: string, stickerId?: number): Promise<any> {
        let url = `${this.apiUrl}/p2p/action?username=${this.getUsername()}&action=${action}`;
        if (stickerId) url += `&stickerId=${stickerId}`;
        return await lastValueFrom(this.http.post<any>(url, {}, { headers: this.getHeaders() }));
    }

    async requestP2PExchange(targetUsername: string): Promise<any> {
        return await lastValueFrom(
            this.http.post<any>(`${this.apiUrl}/p2p/request?requester=${this.getUsername()}&target=${targetUsername}`, {}, { headers: this.getHeaders() })
        );
    }

    async confirmP2PExchange(targetUsername: string, code: string): Promise<any> {
        return await lastValueFrom(
            this.http.post<any>(`${this.apiUrl}/p2p/confirm?requester=${this.getUsername()}&target=${targetUsername}&code=${code}`, {}, { headers: this.getHeaders() })
        );
    }

    async executeP2PExchange(targetUsername: string, myStickerId: number, theirStickerId: number): Promise<any> {
        const url = `${this.apiUrl}/p2p/execute?requester=${this.getUsername()}&target=${targetUsername}&myStickerId=${myStickerId}&theirStickerId=${theirStickerId}`;
        const result = await lastValueFrom(this.http.post<any>(url, {}, { headers: this.getHeaders() }));
        this.loadAlbumFromServer();
        return result;
    }

    private getHeaders() {
        const token = localStorage.getItem('authToken');
        return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    }

    private getUsername() {
        return localStorage.getItem('username') || 'normaluser';
    }
    clearAlbumState() {
        this.pagesSubject.next([]);
        this.statusSubject.next({ availablePacks: 0, coins: 0 });
    }

    loadAlbumFromServer() {
        this.http.get<any[]>(`${this.apiUrl}/my-album?username=${this.getUsername()}`, { headers: this.getHeaders() })
            .subscribe({
                next: (data) => this.pagesSubject.next(this.groupByPage(data)),
                error: (err) => console.error('Error cargando el álbum', err)
            });
    }

    loadStatusFromServer() {
        this.http.get<AlbumStatus>(`${this.apiUrl}/status?username=${this.getUsername()}`, { headers: this.getHeaders() })
            .subscribe({
                next: (data) => this.statusSubject.next(data),
                error: (err) => console.error('Error cargando estado', err)
            });
    }

    private groupByPage(stickers: any[]): AlbumPage[] {
        const pagesMap = new Map<string, AlbumPage>();
        stickers.forEach(s => {
            const pageId = `${s.sectionId}-${s.pageTitle}`;
            if (!pagesMap.has(pageId)) {
                pagesMap.set(pageId, { id: pageId, sectionId: s.sectionId, pageNumber: pagesMap.size + 1, title: s.pageTitle, slots: [] });
            }
            pagesMap.get(pageId)!.slots.push(s);
        });
        return Array.from(pagesMap.values());
    }

    getPagesBySection(sectionId: string): AlbumPage[] {
        return this.pagesSubject.value.filter(p => p.sectionId === sectionId);
    }

    getSectionProgress(sectionId: string) {
        const pages = this.getPagesBySection(sectionId);
        const allSlots = pages.flatMap(p => p.slots);
        const total = allSlots.length;
        const collected = allSlots.filter(s => s.owned).length;
        return { collected, total, percent: total === 0 ? 0 : Math.round((collected / total) * 100) };
    }

    async buyPackOnServer(): Promise<any> {
        const result = await lastValueFrom(this.http.post<any>(`${this.apiUrl}/buy-pack?username=${this.getUsername()}`, {}, { headers: this.getHeaders() }));
        this.loadStatusFromServer();
        return result;
    }
    async getAvailablePacksCount(): Promise<number> {
        return this.statusSubject.value.availablePacks;
    }

    async openPackOnServer(): Promise<any[]> {
        const results = await lastValueFrom(this.http.post<any[]>(`${this.apiUrl}/open?username=${this.getUsername()}`, {}, { headers: this.getHeaders() }));
        this.loadAlbumFromServer();
        this.loadStatusFromServer();
        return results;
    }

    async exchangeDuplicatesOnServer(): Promise<any> {
        const result = await lastValueFrom(this.http.post<any>(`${this.apiUrl}/exchange?username=${this.getUsername()}`, {}, { headers: this.getHeaders() }));
        this.loadAlbumFromServer();
        this.loadStatusFromServer();
        return result;
    }

    async win1000(): Promise<any> {
        const result = await lastValueFrom(this.http.post<any>(`${this.apiUrl}/win1000?username=${this.getUsername()}`, {}, { headers: this.getHeaders() }));
        this.loadAlbumFromServer();
        this.loadStatusFromServer();
        return result;
    }

    async getTransactionHistory(): Promise<Transaction[]> {
        return lastValueFrom(this.http.get<Transaction[]>(`${this.apiUrl}/transactions?username=${this.getUsername()}`, { headers: this.getHeaders() }));
    }

    refreshUserData() {
        this.clearAlbumState();
        this.loadAlbumFromServer();
        this.loadStatusFromServer();
    }
}