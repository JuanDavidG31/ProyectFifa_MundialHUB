import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

export interface PlayerStat {
  name: string;
  team: string;
  flagUrl: string;
  value: number;
}

export interface LiveMatch {
  homeTeam: string; homeFlag: string; homeScore: number;
  awayTeam: string; awayFlag: string; awayScore: number;
  minute: string; events: string[];
}

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './estadisticas.component.html',
  styleUrls: ['./estadisticas.component.scss']
})
export class EstadisticasComponent implements OnInit {

  // 🌟 CAMBIO 1: Ahora el tab por defecto es 'goles'
  activeTab: 'envivo' | 'goles' | 'asistencias' = 'goles';
  isLoading = false;

  topScorers: PlayerStat[] = [];
  topAssists: PlayerStat[] = [];
  currentList: PlayerStat[] = [];

  // Mantenemos el partido en vivo simulado
  liveMatches: LiveMatch[] = [
    {
      homeTeam: 'Argentina', homeFlag: 'https://crests.football-data.org/762.png', homeScore: 2,
      awayTeam: 'Francia', awayFlag: 'https://crests.football-data.org/773.svg', awayScore: 1,
      minute: '75\'',
      events: [
        '⚽ 23\' L. Messi (ARG)',
        '⚽ 36\' A. Di María (ARG)',
        '⚽ 71\' K. Mbappé (FRA)',
        '🟨 74\' E. Fernández (ARG)'
      ]
    }
  ];

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.cargarEstadisticas(); 
  }

  // 🌟 CAMBIO 2: Lógica de carga optimizada (sin tarjetas)
  cargarEstadisticas() {
    this.isLoading = true;
    let requestsCompleted = 0;

    // Función interna para apagar el spinner cuando terminen las 2 peticiones
    const checkCompletion = () => {
      requestsCompleted++;
      if (requestsCompleted === 2) {
        this.isLoading = false;
      }
    };

    // A) Traer Goleadores
    this.authService.getTopScorers().subscribe({
      next: (data) => {
        this.topScorers = data;
        if (this.activeTab === 'goles') this.updateList();
        checkCompletion();
      },
      error: (err) => {
        console.error("Error cargando goleadores:", err);
        checkCompletion();
      }
    });

    // B) Traer Asistencias
    this.authService.getTopAssists().subscribe({
      next: (data) => {
        this.topAssists = data;
        if (this.activeTab === 'asistencias') this.updateList();
        checkCompletion();
      },
      error: (err) => {
        console.error("Error cargando asistencias:", err);
        checkCompletion();
      }
    });
  }

  setTab(tab: 'envivo' | 'goles' | 'asistencias') {
    this.activeTab = tab;
    this.updateList();
  }

  updateList() {
    if (this.activeTab === 'goles') this.currentList = this.topScorers;
    else if (this.activeTab === 'asistencias') this.currentList = this.topAssists;
  }
}