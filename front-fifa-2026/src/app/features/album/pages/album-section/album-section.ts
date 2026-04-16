import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AlbumService } from '../../services/album.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { AlbumPage, StickerSlot } from '../../models/album.models';

@Component({
  selector: 'app-album-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './album-section.html',
  styleUrl: './album-section.scss',
})
export class AlbumSection implements OnInit, OnDestroy {
  sectionId!: string;
  sectionTitle = '';
  pages: AlbumPage[] = [];

  collected = 0;
  total = 0;
  percent = 0;

  activePageIndex = 0;
  selectedSticker: StickerSlot | null = null;

  openDetail(sticker: StickerSlot) {
    this.selectedSticker = sticker;
  }

  closeDetail() {
    this.selectedSticker = null;
  }

  private sub?: Subscription;

  private titleMap: Record<string, string> = {
    'selecciones': 'Equipos y Jugadores 2026',
    'estadios': 'Estadios Oficiales',
    'categorias': 'Ediciones Especiales'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private albumService: AlbumService
  ) { }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe(params => {
      this.sectionId = params.get('id') ?? '';
      this.sectionTitle = this.titleMap[this.sectionId] || this.sectionId.toUpperCase();

      this.pages = this.albumService.getPagesBySection(this.sectionId);
      const progress = this.albumService.getSectionProgress(this.sectionId);
      this.collected = progress.collected;
      this.total = progress.total;
      this.percent = progress.percent;

      this.activePageIndex = 0;
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  goBack(): void {
    this.router.navigate(['/album']);
  }

  setActivePage(index: number): void {
    this.activePageIndex = index;
  }

  get activePage(): AlbumPage | null {
    return this.pages[this.activePageIndex] ?? null;
  }

  prevPage() {
    if (this.activePageIndex > 0) this.setActivePage(this.activePageIndex - 1);
  }

  nextPage() {
    if (this.activePageIndex < this.pages.length - 1) this.setActivePage(this.activePageIndex + 1);
  }
}