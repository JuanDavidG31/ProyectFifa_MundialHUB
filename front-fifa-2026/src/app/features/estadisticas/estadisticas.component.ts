import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { interval, Subscription } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { MatchesService } from '../../core/services/matches.service';
import { StatsService } from '../../core/services/stats.service';
export interface PlayerStat {
  name: string;
  team: string;
  flagUrl: string;
  value: number;
}


export interface LiveMatch {
  id: number;
  utcDate: string;
  status: string;
  stage: string;
  homeTeam: string; homeCrest: string;
  awayTeam: string; awayCrest: string;
  score: {
    duration: string;
    fullTime: { home: number | null, away: number | null };
    halfTime: { home: number | null, away: number | null };
    extraTime: { home: number | null, away: number | null };
    penalties: { home: number | null, away: number | null };
  };
}

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './estadisticas.component.html',
  styleUrls: ['./estadisticas.component.scss']
})
export class EstadisticasComponent implements OnInit, OnDestroy {

  activeTab: 'envivo' | 'goles' | 'asistencias' = 'goles';
  isLoading = false;

  topScorers: PlayerStat[] = [];
  topAssists: PlayerStat[] = [];
  currentList: PlayerStat[] = [];
  private refreshSubscription!: Subscription;
  liveMatchesList: LiveMatch[] = [];
  selectedMatch: LiveMatch | null = null;


  constructor(private authService: AuthService, private matchesService: MatchesService, private statsService: StatsService) { }

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.cargarEstadisticas();
    this.iniciarActualizacionAutomatica();
  }

  ngOnDestroy(): void {

    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }
  cerrarSesion() {
    this.authService.logout();
  }
  openMatchDetails(match: LiveMatch) {
    this.selectedMatch = match;
    document.body.style.overflow = 'hidden';
  }

  closeMatchDetails() {
    this.selectedMatch = null;
    document.body.style.overflow = 'auto';
  }



  cargarEstadisticas() {
    this.isLoading = true;
    let requestsCompleted = 0;

    const checkCompletion = () => {
      requestsCompleted++;
      if (requestsCompleted === 3) {
        this.isLoading = false;
      }
    };

    this.statsService.getTopScorers().subscribe({
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

    this.statsService.getTopAssists().subscribe({
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

    this.matchesService.getAllMatches().subscribe({
      next: (data: LiveMatch[]) => {
        this.ordenarPartidos(data); 
        checkCompletion();
      },
      error: (err) => {
        console.error("Error cargando los partidos en vivo:", err);
        checkCompletion();
      }
    });
  }

  iniciarActualizacionAutomatica() {
    this.refreshSubscription = interval(30000)
      .pipe(
        filter(() => this.activeTab === 'envivo'),


        switchMap(() => this.matchesService.getAllMatches())
      )
      .subscribe({
        next: (data: LiveMatch[]) => {
          this.ordenarPartidos(data);
        },
        error: (err) => console.error("Error en actualización silenciosa:", err)
      });
  }

  ordenarPartidos(data: LiveMatch[]) {
    const statusPriority: { [key: string]: number } = {
      'IN_PLAY': 1,
      'PAUSED': 1,
      'TIMED': 2,
      'SCHEDULED': 2,
      'FINISHED': 3
    };

    this.liveMatchesList = data.sort((a, b) => {
      const priorityA = statusPriority[a.status] || 4;
      const priorityB = statusPriority[b.status] || 4;

      if (priorityA !== priorityB) {
        return priorityA - priorityB;
      }
      if (priorityA === 3) {
        return new Date(b.utcDate).getTime() - new Date(a.utcDate).getTime();
      }
      return new Date(a.utcDate).getTime() - new Date(b.utcDate).getTime();
    });
  }



  translateStatus(status: string): string {
    const states: any = {
      'FINISHED': 'Finalizado',
      'IN_PLAY': 'En Vivo',
      'PAUSED': 'Medio Tiempo',
      'SCHEDULED': 'Programado',
      'TIMED': 'Por Empezar'
    };
    return states[status] || status;
  }

  setTab(tab: 'envivo' | 'goles' | 'asistencias') {
    this.activeTab = tab;
    this.updateList();
  }

  updateList() {
    if (this.activeTab === 'goles') this.currentList = this.topScorers;
    else if (this.activeTab === 'asistencias') this.currentList = this.topAssists;
  }

  trackByMatchId(index: number, match: LiveMatch): number {
    return match.id;
  }
}