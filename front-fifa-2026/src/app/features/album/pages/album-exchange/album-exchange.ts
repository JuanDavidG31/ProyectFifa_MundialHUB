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
  totalDuplicates = 0;
  potentialCoins = 0;
  isLoading = false;
  showSuccessModal = false;
  coinsGained = 0;

  p2pTargetUser = '';
  myUsername = localStorage.getItem('username') || '';
  currentRoom: any = null;
  showP2PSuccess = false;
  private roomSubscription!: Subscription;
  isInviting = false;
  isTrading = false; 
  isFetchingInventories = false;
  isSettingReady = false;
  constructor(private albumService: AlbumService) { }

  ngOnInit() {
    this.calculateDuplicates();
    this.albumService.connectToP2PWebSocket();

    this.roomSubscription = this.albumService.p2pRoom$.subscribe((room) => {
      if (room && room.status === 'ACCEPTED' && this.currentRoom?.status !== 'ACCEPTED') {
        this.isFetchingInventories = true;
        setTimeout(() => {
          this.isFetchingInventories = false;
        }, 2000);
      }

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
    if (action === 'ACCEPT') {
      this.isFetchingInventories = true; 
    }

    try {
      await this.albumService.actionP2P(action);
      this.currentRoom = await this.albumService.pollP2PRoom();
    } catch (e) { 
      console.error(e); 
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
    this.isSettingReady = true; 
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
      this.isSettingReady = false; 
    }
  }

  async cancelExchange() {
    try {
      await this.albumService.actionP2P('LEAVE');
      this.currentRoom = null;
    } catch (e) { console.error(e); }
  }

  startTradeAnimation() {
    this.isTrading = true;

    setTimeout(() => {
      this.isTrading = false;
      this.showP2PSuccess = true; 
      this.albumService.actionP2P('LEAVE'); 
      this.calculateDuplicates(); 

      setTimeout(() => {
        this.closeModal(); 
        this.currentRoom = null; 
      }, 2500);

    }, 4000); 
  }

  // FUNCIONES DEL MERCADO 

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