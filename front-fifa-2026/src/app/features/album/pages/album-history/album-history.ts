import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AlbumService } from '../../services/album.service';
import { Transaction } from '../../models/album.models';

@Component({
  selector: 'app-album-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './album-history.html',
  styleUrls: ['./album-history.scss']
})
export class AlbumHistory implements OnInit {
  transactions: Transaction[] = [];
  isLoading = true;

  constructor(private albumService: AlbumService) { }

  async ngOnInit() {
    try {
      this.transactions = await this.albumService.getTransactionHistory();
    } catch (error) {
      console.error("Error cargando historial", error);
    } finally {
      this.isLoading = false;
    }
  }

  getTransactionFormat(type: string): { icon: string, colorClass: string, label: string } {
    switch (type) {
      case 'PACK_OPENED':
        return { icon: '📦', colorClass: 'neutral', label: 'Paquete Abierto' };
      case 'DUPLICATES_EXCHANGED':
        return { icon: '🔄', colorClass: 'positive', label: 'Láminas Quemadas' };
      case 'PACK_BOUGHT':
        return { icon: '🛒', colorClass: 'negative', label: 'Paquete Comprado' };
      case 'ALBUM_COMPLETED_REWARD':
        return { icon: '🏆', colorClass: 'positive', label: 'Premio Álbum 100%' };
      case 'P2P_EXCHANGE':
        return { icon: '🤝', colorClass: 'neutral', label: 'Intercambio P2P' };
      default:
        return { icon: '❓', colorClass: 'neutral', label: 'Movimiento' };
    }
  }
}