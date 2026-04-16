import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './tickets.component.html',
  styleUrls: ['./tickets.component.scss']
})
export class TicketsComponent implements OnInit {
  processingId: number | null = null;
  partidos: any[] = [];
  gruposDePartidos: { nombre: string, partidos: any[] }[] = [];
  isLoadingMatches = false;
  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.cargarPartidosReales();
  }

  cargarPartidosReales() {
    this.isLoadingMatches = true;
    this.authService.getWcMatches().subscribe({
      next: (data) => {
        this.partidos = data;
        this.agruparPartidos();
        this.isLoadingMatches = false;
      },
      error: (err) => {
        console.error("Error cargando partidos:", err);
        this.isLoadingMatches = false;
        alert("Hubo un error cargando los partidos del mundial.");
      }
    });
  }

  agruparPartidos() {
    const gruposMap = new Map<string, any[]>();

    this.partidos.forEach(partido => {
      const nombreGrupo = partido.grupo || 'Otros';
      if (!gruposMap.has(nombreGrupo)) {
        gruposMap.set(nombreGrupo, []);
      }
      gruposMap.get(nombreGrupo)?.push(partido);
    });

    this.gruposDePartidos = Array.from(gruposMap.keys()).map(nombre => ({
      nombre: nombre,
      partidos: gruposMap.get(nombre) || []
    })).sort((a, b) => {
      const pesoA = this.obtenerPesoFase(a.nombre);
      const pesoB = this.obtenerPesoFase(b.nombre);

      if (pesoA !== pesoB) {
        return pesoA - pesoB;
      }

      return a.nombre.localeCompare(b.nombre);
    });
  }

  obtenerPesoFase(nombre: string): number {
    const nombreUpper = nombre.toUpperCase();

    if (nombreUpper.includes('GRUPO')) return 1;
    if (nombreUpper.includes('32')) return 2; // LAST 32
    if (nombreUpper.includes('16')) return 3; // LAST 16
    if (nombreUpper.includes('QUARTER') || nombreUpper.includes('CUARTOS')) return 4;
    if (nombreUpper.includes('SEMI')) return 5;
    if (nombreUpper.includes('THIRD') || nombreUpper.includes('TERCER')) return 6;
    if (nombreUpper.includes('FINAL')) return 7;

    return 99;
  }

  comprar(partidoId: number) {
    const partido = this.partidos.find(p => p.id === partidoId);
    if (!partido) return;

    this.processingId = partidoId;


    const currentUser = localStorage.getItem('userName') || 'usuario_desconocido';

    const purchasePayload = {
      userEmail: currentUser,
      matchName: `${partido.local} VS ${partido.visitante}`,
      stadium: partido.estadio,
      date: partido.fecha
    };

    this.authService.buyTicket(purchasePayload).subscribe({
      next: (res: any) => {
        alert('🎟️ ¡Compra exitosa! ' + res.message);
        this.processingId = null; 
        
      
        this.cargarPartidosReales(); 
      },
      error: (err) => {
        alert('❌ ' + (err.error?.error || err.message));
        this.processingId = null;
      }
    });
  }
}