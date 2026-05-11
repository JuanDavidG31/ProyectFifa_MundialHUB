import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BettingService, BetMessageDTO } from '../../core/services/betting.service';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

export interface MatchPrediction {
  id: number;
  homeTeam: string;
  homeFlag: string;
  awayTeam: string;
  awayFlag: string;
  date: string;
  group: string;
  homeScorePred: number | null;
  awayScorePred: number | null;
  isSaved: boolean;
  status: 'PENDING' | 'LOCKED' | 'FINISHED';
}

@Component({
  selector: 'app-apuestas',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './apuestas.component.html',
  styleUrls: ['./apuestas.component.scss']
})
export class ApuestasComponent implements OnInit {

  activeTab: 'proximos' | 'historial' | 'salas' = 'proximos'; // <-- Añadido 'salas'

  availableRooms: any[] = [];
  currentRoom: any = null;
  joinRequests: BetMessageDTO[] = []; // Solicitudes pendientes
  myUsername = localStorage.getItem('username') || 'Jugador1'; // Ajusta según tu lógica

  isCreateRoomModalOpen = false;
  newRoomName = '';
  userPoints = 1250;
  userRank = 34;
  accuracy = 68;

  upcomingMatches: MatchPrediction[] = [
    {
      id: 1, homeTeam: 'Argentina', homeFlag: 'https://crests.football-data.org/762.png',
      awayTeam: 'España', awayFlag: 'https://crests.football-data.org/760.svg',
      date: 'Mañana, 15:00', group: 'Semifinal',
      homeScorePred: null, awayScorePred: null, isSaved: false, status: 'PENDING'
    },
    {
      id: 2, homeTeam: 'Francia', homeFlag: 'https://crests.football-data.org/773.svg',
      awayTeam: 'Inglaterra', awayFlag: 'https://crests.football-data.org/770.svg',
      date: 'Mañana, 19:00', group: 'Semifinal',
      homeScorePred: null, awayScorePred: null, isSaved: false, status: 'PENDING'
    },
    {
      id: 3, homeTeam: 'Brasil', homeFlag: 'https://crests.football-data.org/764.svg',
      awayTeam: 'Portugal', awayFlag: 'https://crests.football-data.org/765.svg',
      date: 'Hoy, 21:00', group: 'Cuartos de Final',
      homeScorePred: 2, awayScorePred: 1, isSaved: true, status: 'LOCKED'
    }
  ];
  private subs: Subscription = new Subscription();
  constructor(
    private bettingService: BettingService,
    private authService: AuthService,
    private router: Router
  ) { }
  ngOnInit(): void {
    // this.router.navigate(['/home']);

    window.scrollTo(0, 0);
    this.bettingService.connect();

    this.bettingService.recoverRoom(this.myUsername).subscribe(room => {
      if (room && room.id) {
        this.bettingService.currentRoom$.next(room);
      }
    });

    this.subs.add(
      this.bettingService.currentRoom$.subscribe(room => {
        this.currentRoom = room;

        if (!room && this.activeTab === 'salas') {
          this.loadRooms();
        }
      })
    );

    this.subs.add(
      this.bettingService.joinRequests$.subscribe(reqs => {
        this.joinRequests = reqs;
      })
    );

    this.subs.add(
      this.bettingService.notifications$.subscribe(msg => {
        alert(msg);
      })
    );
  }

  cerrarSesion() {
    this.authService.logout();
  }

  ngOnDestroy(): void {

    this.subs.unsubscribe();
  }

  openCreateRoomModal() {
    this.isCreateRoomModalOpen = true;
  }

  closeCreateRoomModal() {
    this.isCreateRoomModalOpen = false;
    this.newRoomName = '';
  }

  confirmCreateRoom() {
    if (this.newRoomName.trim()) {
      this.bettingService.createRoom(this.newRoomName.trim(), this.myUsername).subscribe({
        next: (room) => {
          this.bettingService.currentRoom$.next(room);
          this.closeCreateRoomModal();
          alert('¡Sala creada! Eres el administrador.');
        },
        error: (err) => {

          alert(err.error?.error || 'Error al crear la sala. Verifica el nombre.');
        }
      });
    }
  }


  setTab(tab: 'proximos' | 'historial' | 'salas') {
    this.activeTab = tab;
    if (tab === 'salas') {
      this.loadRooms();
    }
  }

  loadRooms() {
    this.bettingService.getAvailableRooms().subscribe(rooms => {
      this.availableRooms = rooms;
    });
  }

  createRoom() {
    const name = prompt('Ingresa el nombre de tu sala:');
    if (name) {
      this.bettingService.createRoom(name, this.myUsername).subscribe(room => {
        this.currentRoom = room;
        alert('¡Sala creada! Eres el administrador.');
      });
    }
  }

  requestJoin(roomId: string) {
    this.bettingService.sendJoinRequest(roomId, this.myUsername);
    alert('Solicitud enviada al administrador de la sala. Esperando aprobación...');
  }

  acceptUser(req: BetMessageDTO, accept: boolean) {
    this.bettingService.respondToRequest(req.roomId!, this.myUsername, req.sender!, accept);
  }

  savePrediction(match: MatchPrediction) {
    if (match.homeScorePred === null || match.awayScorePred === null) {
      alert("Por favor ingresa ambos resultados antes de guardar.");
      return;
    }
    match.isSaved = true;

    if (this.currentRoom) {
      this.bettingService.sharePrediction(this.currentRoom.id, this.myUsername, match);
      alert('¡Pronóstico guardado y compartido en la sala!');
    }

    const btn = document.getElementById('btn-save-' + match.id);
    if (btn) { /* ... tu lógica anterior de UI ... */ }
  }

  salirDeSala() {
    if (this.currentRoom) {
      this.bettingService.leaveRoom(this.currentRoom.id, this.myUsername);
    }
  }




}