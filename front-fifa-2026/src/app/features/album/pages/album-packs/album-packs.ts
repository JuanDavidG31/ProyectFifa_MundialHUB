import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AlbumService } from '../../services/album.service';
import { Pack } from '../../models/album.models';
import { Subscription } from 'rxjs';

type OpenStep = 'IDLE' | 'RESULTS';

@Component({
  selector: 'app-album-packs',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './album-packs.html',
  styleUrls: ['./album-packs.scss']
})
export class AlbumPacks implements OnInit, OnDestroy {

  packs: Pack[] = [];
  index = 0;
  coins = 0; 
  availablePacks = 0;
  isBuyingPack = false; 
  openStep: OpenStep = 'IDLE';
  openingPackId: string | null = null;
  lastStickers: any[] = [];
isRipping = false;
  private shakeMs = 1200;
  isShaking = false;
  private statusSub!: Subscription;

  constructor(private albumService: AlbumService) { }

  ngOnInit() {
    this.statusSub = this.albumService.status$.subscribe(status => {
      this.coins = status.coins; 
      this.availablePacks = status.availablePacks;
      this.generatePacks(status.availablePacks);
    });
  }

  ngOnDestroy() {
    if (this.statusSub) this.statusSub.unsubscribe();
  }

  generatePacks(count: number) {
    this.packs = [];
    for (let i = 0; i < count; i++) {
      this.packs.push({ id: `pack-${i}`, status: 'UNOPENED', size: 5 });
    }
    if (this.index >= this.packs.length && this.packs.length > 0) {
      this.index = this.packs.length - 1;
    }
  }

  get current(): Pack | null {
    return this.packs.length ? this.packs[this.index] : null;
  }

  prev(): void {
    if (!this.packs.length) return;
    this.index = (this.index - 1 + this.packs.length) % this.packs.length;
  }

  next(): void {
    if (!this.packs.length) return;
    this.index = (this.index + 1) % this.packs.length;
  }

  
  async buyPack() {
    // Si no hay dinero o ya se está comprando, no hacemos nada
    if (this.coins < 10 || this.isBuyingPack) return;

    this.isBuyingPack = true; // ⬅️ ENCENDEMOS EL SPINNER

    try {
      await this.albumService.buyPackOnServer();
    } catch (e: any) {
      console.error(e);
      alert(e.error?.error || "Error al comprar el paquete.");
    } finally {
      this.isBuyingPack = false; // ⬅️ APAGAMOS EL SPINNER AL TERMINAR (haya error o no)
    }
  }

  async openCurrent() {
    const p = this.current;
    if (!p || p.status === 'OPENED' || this.isShaking || this.isRipping) return;

    this.openingPackId = p.id;
    this.isShaking = true; // Fase 1: Empieza a temblar

    try {
      const results = await this.albumService.openPackOnServer();

      // Transición quirúrgica entre temblar y rasgar
      setTimeout(() => {
        this.isShaking = false;
        this.isRipping = true; // Fase 2: Se dispara el rasgado visual
      }, 700); // Tiembla durante 700ms

      // Al finalizar todo el tiempo (1200ms totales) mostramos las láminas
      setTimeout(() => {
        this.lastStickers = results.map(s => ({
          ...s,
          isDuplicate: s.duplicates > 0
        }));

        p.status = 'OPENED';
        this.openStep = 'RESULTS';
        this.isRipping = false; // Reseteamos la animación
        this.openingPackId = null;
      }, this.shakeMs);

    } catch (e: any) {
      console.error(e);
      alert(e.error?.error || "Error al abrir el paquete.");
      this.isShaking = false;
      this.isRipping = false;
      this.openingPackId = null;
    }
  }

  closeOverlay(): void {
    this.openStep = 'IDLE';
    this.openingPackId = null;
    this.lastStickers = [];
  }

  onImgError(ev: Event) {
    const img = ev.target as HTMLImageElement;
    img.src = '/stickers/1.jpg';
  }
}