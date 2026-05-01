import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms'; // <-- Importamos FormsModule
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], // <-- Lo agregamos a los imports
  templateUrl: './tickets.component.html',
  styleUrls: ['./tickets.component.scss']
})
export class TicketsComponent implements OnInit {
  processingId: number | null = null;
  partidosRaw: any[] = []; // <-- Guardará la lista original intacta
  partidos: any[] = [];
  gruposDePartidos: { nombre: string, partidos: any[] }[] = [];
  isLoadingMatches = false;

  // Variables para filtros
  searchTerm: string = '';
  sortOption: string = 'default';
  private monthMap: { [key: string]: string } = {
    'ene': '01', 'feb': '02', 'mar': '03', 'abr': '04', 'may': '05', 'jun': '06',
    'jul': '07', 'ago': '08', 'sep': '09', 'oct': '10', 'nov': '11', 'dic': '12'
  };
  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.cargarPartidosReales();
  }

  formatearFechaParaBackend(fechaSucia: string): string {
    try {
      // Si la fecha es "16 jun 2026 - 20:00", la dividimos por el espacio
      const partes = fechaSucia.split(' ');
      const dia = partes[0].padStart(2, '0'); // "16"
      const mesTexto = partes[1].toLowerCase(); // "jun"
      const anio = partes[2]; // "2026"

      const mesNumero = this.monthMap[mesTexto] || '01';

      return `${anio}-${mesNumero}-${dia}`; // Retorna "2026-06-16"
    } catch (e) {
      console.error("Error formateando fecha:", e);
      return fechaSucia; // Si falla, devuelve la original por si acaso
    }
  }

  cargarPartidosReales() {
    this.isLoadingMatches = true;
    this.authService.getWcMatches().subscribe({
      next: (data) => {
        this.partidosRaw = data; // Guardamos la data original
        this.aplicarFiltros(); // Llamamos a los filtros en lugar de agrupar directamente
        this.isLoadingMatches = false;
      },
      error: (err) => {
        console.error("Error cargando partidos:", err);
        this.isLoadingMatches = false;
        alert("Hubo un error cargando los partidos del mundial.");
      }
    });
  }

  // Nueva función mágica que filtra y ordena
  // Nueva función mágica que filtra y ordena
  aplicarFiltros() {
    // 1. Filtrar por texto (busca en equipo local o visitante)
    let filtrados = this.partidosRaw.filter(p =>
      p.local.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      p.visitante.toLowerCase().includes(this.searchTerm.toLowerCase())
    );

    // FUNCIÓN AUXILIAR: Asegura que el precio siempre sea un número matemático real
    const obtenerPrecioNum = (precio: any) => {
      if (!precio) return 0;
      // Convierte a texto, elimina símbolos de moneda si los hay y lo pasa a decimal
      const valor = parseFloat(precio.toString().replace(/[^0-9.-]+/g, ""));
      return isNaN(valor) ? 0 : valor;
    };

    // 2. Ordenar según la opción seleccionada
    if (this.sortOption === 'priceDesc') {
      filtrados.sort((a, b) => obtenerPrecioNum(b.precio) - obtenerPrecioNum(a.precio));
    } else if (this.sortOption === 'priceAsc') {
      filtrados.sort((a, b) => obtenerPrecioNum(a.precio) - obtenerPrecioNum(b.precio));
    } else if (this.sortOption === 'alphaAsc') {
      filtrados.sort((a, b) => a.local.localeCompare(b.local));
    } else if (this.sortOption === 'alphaDesc') {
      filtrados.sort((a, b) => b.local.localeCompare(a.local));
    } else if (this.sortOption === 'default') {
      // Si es por defecto, vuelve al orden original que traía el servidor
      filtrados.sort((a, b) => a.id - b.id);
    }

    this.partidos = filtrados;
    this.agruparPartidos();
  }

  agruparPartidos() {
    // TRUCO DE DISEÑO: Si el usuario está ordenando por precio o alfabéticamente, 
    // agrupamos todo en una sola lista para que el orden global sea evidente y no se rompa por fases.
    if (this.sortOption !== 'default') {
      this.gruposDePartidos = [{
        nombre: 'Resultados Ordenados',
        partidos: this.partidos
      }];
      return; // Detenemos la ejecución aquí
    }

    // Si la opción es "default", hacemos el agrupamiento original por Fases/Grupos
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

  comprar(partidoId: number) {
    const partido = this.partidosRaw.find(p => p.id === partidoId);
    if (!partido) return;

    this.processingId = partidoId;
    const currentUser = localStorage.getItem('username') || 'usuario';

    // --- 1. PREPARAMOS EL TICKET (Formato Original) ---
    const purchasePayload = {
      userEmail: currentUser,
      matchName: `${partido.local} VS ${partido.visitante}`,
      stadium: partido.estadio,
      date: partido.fecha // 🎫 Aquí enviamos "16 jun 2026 - 20:00" tal cual
    };

    // Primero enviamos a la API de Tickets
    this.authService.buyTicket(purchasePayload).subscribe({
      next: (res) => {

        // --- 2. PREPARAMOS EL ITINERARIO (Formato Técnico) ---
        // Solo convertimos la fecha aquí, para el calendario
        const fechaLimpia = this.formatearFechaParaBackend(partido.fecha);

        const eventoItinerario = [{
          userEmail: currentUser,
          eventType: 'MATCH',
          title: `⚽ Partido: ${partido.local} vs ${partido.visitante}`,
          eventDate: fechaLimpia, // 📅 Aquí enviamos "2026-06-16"
          location: partido.estadio
        }];

        // Guardamos en la tabla de itinerario
        this.authService.saveItineraryEvents(eventoItinerario).subscribe({
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