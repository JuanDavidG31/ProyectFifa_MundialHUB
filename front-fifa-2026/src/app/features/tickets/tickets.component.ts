import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms'; 
import { AuthService } from '../../core/services/auth.service';
import { TicketsService } from '../../core/services/ticket.service';
import { MatchesService } from '../../core/services/matches.service';
import { ItineraryService } from '../../core/services/itinerary.service';
@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], 
  templateUrl: './tickets.component.html',
  styleUrls: ['./tickets.component.scss']
})
export class TicketsComponent implements OnInit {
  processingId: number | null = null;
  partidosRaw: any[] = []; 
  partidos: any[] = [];
  gruposDePartidos: { nombre: string, partidos: any[] }[] = [];
  isLoadingMatches = false;

  searchTerm: string = '';
  sortOption: string = 'default';
  private monthMap: { [key: string]: string } = {
    'ene': '01', 'feb': '02', 'mar': '03', 'abr': '04', 'may': '05', 'jun': '06',
    'jul': '07', 'ago': '08', 'sep': '09', 'oct': '10', 'nov': '11', 'dic': '12'
  };
  constructor(private authService: AuthService, private ticketsService: TicketsService, private matchesService: MatchesService, private itineraryService: ItineraryService) { }

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.cargarPartidosReales();
  }

  formatearFechaParaBackend(fechaSucia: string): string {
    try {
      const partes = fechaSucia.split(' ');
      const dia = partes[0].padStart(2, '0'); 
      const mesTexto = partes[1].toLowerCase(); 
      const anio = partes[2]; 

      const mesNumero = this.monthMap[mesTexto] || '01';

      return `${anio}-${mesNumero}-${dia}`; 
    } catch (e) {
      console.error("Error formateando fecha:", e);
      return fechaSucia; 
    }
  }

  cargarPartidosReales() {
    this.isLoadingMatches = true;
    this.matchesService.getWcMatches().subscribe({
      next: (data) => {
        this.partidosRaw = data; 
        this.aplicarFiltros(); 
        this.isLoadingMatches = false;
      },
      error: (err) => {
        console.error("Error cargando partidos:", err);
        this.isLoadingMatches = false;
        alert("Hubo un error cargando los partidos del mundial.");
      }
    });
  }

  aplicarFiltros() {
    let filtrados = this.partidosRaw.filter(p =>
      p.local.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      p.visitante.toLowerCase().includes(this.searchTerm.toLowerCase())
    );

    const obtenerPrecioNum = (precio: any) => {
      if (!precio) return 0;
      const valor = parseFloat(precio.toString().replace(/[^0-9.-]+/g, ""));
      return isNaN(valor) ? 0 : valor;
    };

    if (this.sortOption === 'priceDesc') {
      filtrados.sort((a, b) => obtenerPrecioNum(b.precio) - obtenerPrecioNum(a.precio));
    } else if (this.sortOption === 'priceAsc') {
      filtrados.sort((a, b) => obtenerPrecioNum(a.precio) - obtenerPrecioNum(b.precio));
    } else if (this.sortOption === 'alphaAsc') {
      filtrados.sort((a, b) => a.local.localeCompare(b.local));
    } else if (this.sortOption === 'alphaDesc') {
      filtrados.sort((a, b) => b.local.localeCompare(a.local));
    } else if (this.sortOption === 'default') {
      filtrados.sort((a, b) => a.id - b.id);
    }

    this.partidos = filtrados;
    this.agruparPartidos();
  }

  agruparPartidos() {
    if (this.sortOption !== 'default') {
      this.gruposDePartidos = [{
        nombre: 'Resultados Ordenados',
        partidos: this.partidos
      }];
      return; 
    }

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
    if (nombreUpper.includes('32')) return 2;
    if (nombreUpper.includes('16')) return 3;
    if (nombreUpper.includes('QUARTER') || nombreUpper.includes('CUARTOS')) return 4;
    if (nombreUpper.includes('SEMI')) return 5;
    if (nombreUpper.includes('THIRD') || nombreUpper.includes('TERCER')) return 6;
    if (nombreUpper.includes('FINAL')) return 7;
    return 99;
  }
  cerrarSesion() {
    this.authService.logout();
  }
  comprar(partidoId: number) {
    const partido = this.partidosRaw.find(p => p.id === partidoId);
    if (!partido) return;

    this.processingId = partidoId;
    const currentUser = localStorage.getItem('username') || 'usuario';

    const purchasePayload = {
      userEmail: currentUser,
      matchName: `${partido.local} VS ${partido.visitante}`,
      stadium: partido.estadio,
      date: partido.fecha 
    };

    this.ticketsService.buyTicket(purchasePayload).subscribe({
      next: (res) => {

        
        const fechaLimpia = this.formatearFechaParaBackend(partido.fecha);

        const eventoItinerario = [{
          userEmail: currentUser,
          eventType: 'MATCH',
          title: `Partido: ${partido.local} vs ${partido.visitante}`,
          eventDate: fechaLimpia, 
          location: partido.estadio
        }];

        this.itineraryService.saveItineraryEvents(eventoItinerario).subscribe({
          next: () => {
            this.processingId = null;
            alert("¡Compra exitosa! Ticket enviado y partido agendado.");
          },
          error: (err) => {
            console.error("Error en itinerario:", err);
            this.processingId = null;
            alert("Ticket comprado, pero no se pudo reflejar en el calendario.");
          }
        });
      },
      error: (err) => {
        this.processingId = null;
        alert("Error al procesar la compra.");
      }
    });
  }
}