import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AlbumService } from '../../services/album.service';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-album-exchange',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './album-exchange.html',
  styleUrls: ['./album-exchange.scss']
})
export class AlbumExchange implements OnInit, OnDestroy {
  // VARIABLES DEL MERCADO
  totalDuplicates = 0;
  potentialCoins = 0;
  isLoading = false;
  showSuccessModal = false;
  coinsGained = 0;

  // VARIABLES P2P
  p2pTargetUser = '';
  myUsername = localStorage.getItem('username') || '';
  currentRoom: any = null;
  showP2PSuccess = false;
  private roomSubscription!: Subscription;
  isInviting = false;
  isTrading = false; // ¡NUEVO! Reemplaza al countdown
  isFetchingInventories = false;
  isSettingReady = false;
  constructor(private albumService: AlbumService) { }

  ngOnInit() {
    this.calculateDuplicates();
    this.albumService.connectToP2PWebSocket();

    this.roomSubscription = this.albumService.p2pRoom$.subscribe((room) => {
      // 1. Si la sala pasa a ACCEPTED y antes no lo estaba, forzamos la pantalla de carga
      if (room && room.status === 'ACCEPTED' && this.currentRoom?.status !== 'ACCEPTED') {
        this.isFetchingInventories = true;
        // Simulamos un retraso de 2 segundos para dar la sensación de "Buscando fichas..."
        setTimeout(() => {
          this.isFetchingInventories = false;
        }, 2000);
      }

      // 2. Disparamos la animación de intercambio
      if (room && room.status === 'EXECUTED' && this.currentRoom?.status !== 'EXECUTED') {
        this.startTradeAnimation();
      }

      this.currentRoom = room;
    });
  }

  ngOnDestroy() {
    if (this.roomSubscription) this.roomSubscription.unsubscribe();
    this.albumService.disconnectP2PWebSocket();
  }

  // ==========================================
  // GETTERS INTELIGENTES
  // ==========================================
  get myInventory() {
    if (!this.currentRoom) return null;
    return this.currentRoom.requester === this.myUsername ? this.currentRoom.requesterInventory : this.currentRoom.targetInventory;
  }

  get theirInventory() {
    if (!this.currentRoom) return null;
    return this.currentRoom.requester === this.myUsername ? this.currentRoom.targetInventory : this.currentRoom.requesterInventory;
  }

  get myReady() {
    if (!this.currentRoom) return false;
    return this.currentRoom.requester === this.myUsername ? this.currentRoom.requesterReady : this.currentRoom.targetReady;
  }

  get theirReady() {
    if (!this.currentRoom) return false;
    return this.currentRoom.requester === this.myUsername ? this.currentRoom.targetReady : this.currentRoom.requesterReady;
  }

  get mySelectedSticker() {
    const id = this.currentRoom?.requester === this.myUsername ? this.currentRoom?.requesterStickerId : this.currentRoom?.targetStickerId;
    return this.myInventory?.find((s: any) => s.id === id);
  }

  get theirSelectedSticker() {
    const id = this.currentRoom?.requester === this.myUsername ? this.currentRoom?.targetStickerId : this.currentRoom?.requesterStickerId;
    return this.theirInventory?.find((s: any) => s.id === id);
  }

  // ==========================================
  // ACCIONES P2P
  // ==========================================
  async inviteUser() {
    if (!this.p2pTargetUser) return;
    this.isInviting = true;
    try {
      await this.albumService.inviteP2P(this.p2pTargetUser);
      this.currentRoom = await this.albumService.pollP2PRoom();
    }
    catch (e: any) { alert(e.error?.error || e.message || 'Error al invitar al usuario'); }
    finally { this.isInviting = false; }
  }

  async answerInvite(action: 'ACCEPT' | 'REJECT') {
    // ¡LA MAGIA!: Si el usuario le da a aceptar, mostramos el radar instantáneamente
    if (action === 'ACCEPT') {
      this.isFetchingInventories = true; 
    }

    try {
      await this.albumService.actionP2P(action);
      this.currentRoom = await this.albumService.pollP2PRoom();
    } catch (e) { 
      console.error(e); 
      // Si el servidor falla, quitamos la pantalla de carga
      this.isFetchingInventories = false; 
    }
  }

  async selectSticker(id: number) {
    try {
      await this.albumService.actionP2P('SELECT', id);
      this.currentRoom = await this.albumService.pollP2PRoom();
    } catch (e) { console.error(e); }
  }

  async setReady() {
    this.isSettingReady = true; // Encendemos la ruedita al hacer clic
    try {
      await this.albumService.actionP2P('READY');
      const updatedRoom = await this.albumService.pollP2PRoom();
      
      if (updatedRoom && updatedRoom.status === 'EXECUTED' && this.currentRoom?.status !== 'EXECUTED') {
        this.startTradeAnimation();
      }
      this.currentRoom = updatedRoom;
    } catch (e) { 
      console.error(e); 
    } finally {
      this.isSettingReady = false; // Apagamos la ruedita cuando el servidor responde
    }
  }

  async cancelExchange() {
    try {
      await this.albumService.actionP2P('LEAVE');
      this.currentRoom = null;
    } catch (e) { console.error(e); }
  }

  // --- ¡NUEVA! ANIMACIÓN DIRECTA POKÉMON ---
  startTradeAnimation() {
    this.isTrading = true;

    // 1. Las cartas giran en la pantalla durante 4 segundos
    setTimeout(() => {
      this.isTrading = false;
      this.showP2PSuccess = true; // Mostramos el modal de éxito
      this.albumService.actionP2P('LEAVE'); // Limpiamos la sala en el backend
      this.calculateDuplicates(); // Recalculamos las cartas

      // 2. ¡NUEVO!: Esperamos 2.5 segundos para que lea el mensaje de éxito y cerramos todo automáticamente
      setTimeout(() => {
        this.closeModal(); // Cierra el modal
        this.currentRoom = null; // Reinicia el estado de la sala a la normalidad
      }, 2500);

    }, 4000); // 4 segundos exactos de animación
  }

  // ==========================================
  // FUNCIONES DEL MERCADO CLÁSICO
  // ==========================================
  calculateDuplicates() {
    this.albumService.pages$.subscribe(pages => {
      this.totalDuplicates = 0;
      this.potentialCoins = 0;
      pages.forEach(p => p.slots.forEach(s => {
        if (s.duplicates > 0) {
          this.totalDuplicates += s.duplicates;
          this.potentialCoins += (s.duplicates * (s.exchangeValue || 10));
        }
      }));
    });
  }

  async doExchange() {
    if (this.totalDuplicates === 0) return;
    this.isLoading = true;
    try {
      const res = await this.albumService.exchangeDuplicatesOnServer();
      this.coinsGained = res.coinsGained;
      this.showSuccessModal = true;
    } catch (e: any) { alert(e.error?.error || "Error al procesar."); }
    finally { this.isLoading = false; }
  }

  closeModal() {
    this.showSuccessModal = false;
    this.showP2PSuccess = false;
  }
}