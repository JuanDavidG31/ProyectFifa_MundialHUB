import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AlbumService } from '../../services/album.service';
import { Subscription } from 'rxjs';
import { AlbumStatus } from '../../models/album.models';

interface AlbumSection {
  id: string; name: string; total: number; collected: number; shortName: string;
}

@Component({
  selector: 'app-album-overview',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './album-overview.html',
  styleUrl: './album-overview.scss',
})
export class AlbumOverview implements OnInit, OnDestroy {
  sections: AlbumSection[] = [];
  status: AlbumStatus = { availablePacks: 0, coins: 0 };

  isLoading: boolean = true;
  private subs: Subscription = new Subscription();
  showRewardModal: boolean = false;
  rewardClaimed: boolean = false;
  constructor(private router: Router, private albumService: AlbumService) { }

  ngOnInit() {
    this.albumService.refreshUserData();
    this.albumService.loadAlbumFromServer();
    this.albumService.loadStatusFromServer();

    this.subs.add(this.albumService.pages$.subscribe(() => {
      this.updateStats();
      setTimeout(() => {
        this.isLoading = false;
      }, 1750);
    }));

    this.subs.add(this.albumService.status$.subscribe(st => this.status = st));
  }


  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  updateStats() {
    const seleccionesData = this.albumService.getSectionProgress('selecciones');
    const estadiosData = this.albumService.getSectionProgress('estadios');
    const categoriasData = this.albumService.getSectionProgress('categorias');

    this.sections = [
      { id: 'selecciones', name: 'Equipos 2026', total: seleccionesData.total, collected: seleccionesData.collected, shortName: 'EQ' },
      { id: 'estadios', name: 'Estadios Oficiales', total: estadiosData.total, collected: estadiosData.collected, shortName: 'ST' },
      { id: 'categorias', name: 'Ediciones Especiales', total: categoriasData.total, collected: categoriasData.collected, shortName: 'SP' },
    ];

    const totalCollected = seleccionesData.collected + estadiosData.collected + categoriasData.collected;
    const totalPossible = seleccionesData.total + estadiosData.total + categoriasData.total;

    if (totalPossible > 0 && totalCollected === totalPossible) {
      this.rewardClaimed = localStorage.getItem('albumCompleteReward') === 'true';
      if (!this.rewardClaimed) {
        this.showRewardModal = true;
      }
    }
  }

  goToSection(id: string) { this.router.navigate(['/album/section', id]); }
  goToPacks() { this.router.navigate(['/album/packs']); }

  getProgress(section: AlbumSection): number {
    if (section.total === 0) return 0;
    return Math.round((section.collected / section.total) * 100);
  }

  claimReward() {
    this.albumService.win1000();

    this.status.coins += 1000;
    this.rewardClaimed = true;
    localStorage.setItem('albumCompleteReward', 'true');
    this.showRewardModal = false;
  }

  closeRewardModal() {
    this.showRewardModal = false;
  }
}