import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-teams',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.scss']
})
export class TeamsComponent implements OnInit {
  isLoading = true;
  userCountry = '';
  
  qualifiedTeams: any[] = [];
  myFixtures: any[] = [];
  todosLosPartidosDelMundial: any[] = [];

  searchTerm: string = '';
  filteredTeams: any[] = [];

  // 🌟 Ya no inyectamos HttpClient, solo usamos tu AuthService
  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    const username = localStorage.getItem('userName') || 'normaluser';

    this.authService.getUserDashboard(username).subscribe({
        next: (data) => {
          // El país inicial es el favorito del usuario
          this.userCountry = data.userCountry || 'Colombia'; 
          
          this.authService.getWcMatches().subscribe({
            next: (matches: any[]) => {
              
              this.todosLosPartidosDelMundial = matches;
              this.filtrarPartidosPorPais(this.userCountry);

              const teamsMap = new Map<string, any>();
              
              matches.forEach(match => {
                if (match.local && match.local !== 'TBD') {
                  teamsMap.set(match.local, { name: match.local, crest: match.localCrest });
                }
                if (match.visitante && match.visitante !== 'TBD') {
                  teamsMap.set(match.visitante, { name: match.visitante, crest: match.visitanteCrest });
                }
              });

              this.qualifiedTeams = Array.from(teamsMap.values()).sort((a, b) => a.name.localeCompare(b.name));
              this.filteredTeams = [...this.qualifiedTeams];
              this.isLoading = false;
            },
            error: (err) => {
              console.error("Error cargando los partidos del mundial", err);
              this.isLoading = false;
            }
          });
        },
        error: (err) => {
          console.error("Error cargando dashboard del usuario:", err);
          this.isLoading = false;
        }
      });
  }

  filterTeams() {
    if (!this.searchTerm) {
      this.filteredTeams = this.qualifiedTeams;
    } else {
      this.filteredTeams = this.qualifiedTeams.filter(t => 
        t.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }

  verPartidosDeEquipo(nombreEquipo: string) {
    this.userCountry = nombreEquipo;
    this.filtrarPartidosPorPais(nombreEquipo);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  filtrarPartidosPorPais(pais: string) {
    this.myFixtures = this.todosLosPartidosDelMundial.filter(match => 
      match.local.toLowerCase() === pais.toLowerCase() || 
      match.visitante.toLowerCase() === pais.toLowerCase()
    );
  }
}