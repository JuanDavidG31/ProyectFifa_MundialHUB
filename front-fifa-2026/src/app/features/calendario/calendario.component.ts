import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

interface ItineraryEvent {
  id: number;
  type: 'FLIGHT' | 'HOTEL' | 'MATCH';
  title: string;
  date: string;
  location: string;
}

interface TravelPackage {
  id: number;
  name: string;
  category: string; // 🌟 Necesario para el diseño
  description: string;
  price: number;
  imageIcon: string;
  includedMatches: string;
  matchesRawData?: any[]; // 🌟 Aquí es donde viven los partidos del paquete
}

// Interfaz para el calendario visual
interface CalendarDay {
  dayNum: number | null;
  dateStr: string | null;
  events: ItineraryEvent[];
}

@Component({
  selector: 'app-calendario',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './calendario.component.html',
  styleUrls: ['./calendario.component.scss']
})
export class CalendarioComponent implements OnInit {
  selectedPackage: any = null;
  isDetailsModalOpen = false;
  allMatches: any[] = [];
  activeTab: 'itinerario' | 'paquetes' = 'itinerario';
  processingId: number | null = null;

  myEvents: ItineraryEvent[] = [];
  showAddEventForm = false;
  newEvent: Partial<ItineraryEvent> = { type: 'MATCH', title: '', date: '', location: '' };

  // Inicializamos vacío para llenarlo dinámicamente
  packages: any[] = [];

  currentDate: Date = new Date();
  weekDays: string[] = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  calendarDays: CalendarDay[] = [];

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.cargarDatosIniciales();
  }

  openPackageDetails(pkg: any) {
    this.selectedPackage = pkg;
    this.isDetailsModalOpen = true;
    // Forzamos el scroll del modal hacia arriba al abrirlo
    window.scrollTo(0, 0);
  }

  closePackageDetails() {
    this.selectedPackage = null;
    this.isDetailsModalOpen = false;
  }

  // --- UTILIDAD PARA CREAR VUELOS IMAGINARIOS ---
  generarFechaVuelo(fechaPartidoStr: string): string {
    // fechaPartidoStr viene formateada como "2026-06-11" por tu función previa
    const date = new Date(fechaPartidoStr + 'T00:00:00'); // Añadimos T00:00:00 para evitar desfases de zona horaria
    date.setDate(date.getDate() - 7); // Restamos 7 días
    return date.toISOString().split('T')[0];
  }

  cargarDatosIniciales() {
    const saved = localStorage.getItem('myItinerary');
    if (saved) this.myEvents = JSON.parse(saved);

    this.authService.getWcMatches().subscribe({
      next: (data) => {
        console.log("📡 Datos recibidos del Back:", data); // 🔍 VERIFICA ESTO EN CONSOLA
        this.allMatches = data;
        this.generarCatalogoDePaquetes();
        this.generateCalendar();
      },
      error: (err) => {
        console.error("❌ Error al traer partidos:", err);
      }
    });
  }

  generarCatalogoDePaquetes() {
    const nuevosPaquetes: TravelPackage[] = [];

    if (!this.allMatches || this.allMatches.length === 0) return;

    // --- 1. PAQUETES POR SELECCIÓN (Todos los países) ---
    const todosLosPaises = new Set<string>();
    this.allMatches.forEach(m => {
      if (m.local) todosLosPaises.add(m.local);
      if (m.visitante) todosLosPaises.add(m.visitante);
    });

    Array.from(todosLosPaises).sort().forEach(pais => {
      const partidosPais = this.allMatches.filter(m =>
        m.local === pais || m.visitante === pais
      );

      nuevosPaquetes.push({
        id: Math.floor(Math.random() * 10000),
        name: `Sigue a ${pais}`,
        category: 'Selección',
        description: `Todos los partidos de ${pais} en el torneo.`,
        price: partidosPais.length * 150, // Precio dinámico
        imageIcon: "⚽",
        includedMatches: partidosPais.map(m => `${m.local} vs ${m.visitante}`).join(', '),
        matchesRawData: partidosPais
      });
    });

    // --- 2. PAQUETES POR GRUPOS (A, B, C...) ---
    const todosLosGrupos = new Set<string>();
    this.allMatches.forEach(m => {
      if (m.grupo && m.grupo.startsWith('Grupo')) todosLosGrupos.add(m.grupo);
    });

    Array.from(todosLosGrupos).sort().forEach(grupo => {
      const partidosGrupo = this.allMatches.filter(m => m.grupo === grupo);

      nuevosPaquetes.push({
        id: Math.floor(Math.random() * 10000),
        name: `Tour ${grupo}`,
        category: 'Fase de Grupos',
        description: `Vive la emoción completa de todos los encuentros del ${grupo}.`,
        price: 800,
        imageIcon: "🏟️",
        includedMatches: `${partidosGrupo.length} partidos de la fase inicial.`,
        matchesRawData: partidosGrupo
      });
    });

    // --- 3. PAQUETE "EL CAMINO A LA GLORIA" (Fase Final) ---
    // Filtramos partidos que no tengan grupo (suelen ser eliminatorias) o nombres de fase final
    const faseFinal = this.allMatches.filter(m => {
      const n = (m.nombre || '').toUpperCase();
      return n.includes('CUARTOS') || n.includes('QUARTER') ||
        n.includes('SEMI') || n.includes('FINAL') || n.includes('TERCER');
    });

    if (faseFinal.length > 0) {
      nuevosPaquetes.push({
        id: 9999,
        name: "Final Experience VIP",
        category: 'Fase Final',
        description: "El paquete definitivo: Cuartos, Semifinales y la Gran Final.",
        price: 4500,
        imageIcon: "🏆",
        includedMatches: "Partidos de eliminación directa y Final.",
        matchesRawData: faseFinal
      });
    }

    this.packages = nuevosPaquetes;
  }

  // 🌟 FUNCIÓN CLAVE: Convierte '11 jun 2026' en '2026-06-11' para que el calendario lo reconozca
  formatearFechaParaCalendario(fechaRaw: string): string {
    if (!fechaRaw) return '';

    // Diccionario de meses para el formato de tu back
    const meses: { [key: string]: string } = {
      'ene': '01', 'feb': '02', 'mar': '03', 'abr': '04', 'may': '05', 'jun': '06',
      'jul': '07', 'ago': '08', 'sep': '09', 'oct': '10', 'nov': '11', 'dic': '12'
    };

    try {
      // Espera algo como "11 jun 2026 - 14:00"
      const partes = fechaRaw.split(' ');
      const dia = partes[0].padStart(2, '0');
      const mes = meses[partes[1].toLowerCase()];
      const anio = partes[2];
      return `${anio}-${mes}-${dia}`;
    } catch (e) {
      console.error("Error formateando fecha:", fechaRaw);
      return '';
    }
  }

  comprarPaquete(pkg: any) {
    this.processingId = pkg.id;
    const currentUser = localStorage.getItem('username') || 'usuario';

    const purchasePayload = {
      userEmail: currentUser,
      matchName: `PAQUETE: ${pkg.name}`,
      stadium: 'Sedes Varias',
      date: 'Múltiples Fechas'
    };

    this.authService.buyTicket(purchasePayload).subscribe({
      next: () => {
        if (pkg.matchesRawData && pkg.matchesRawData.length > 0) {
          pkg.matchesRawData.forEach((match: any) => {
            const fechaPartido = this.formatearFechaParaCalendario(match.fecha);

            // 1. Agregar el Partido
            const eventoPartido: ItineraryEvent = {
              id: match.id,
              type: 'MATCH',
              title: `${match.local} vs ${match.visitante}`,
              date: fechaPartido,
              location: match.estadio
            };

            // 2. ✈️ CREAR EL VUELO IMAGINARIO (Una semana antes)
            const fechaVuelo = this.generarFechaVuelo(fechaPartido);
            const eventoVuelo: ItineraryEvent = {
              id: Math.floor(Math.random() * 1000000), // ID aleatorio para el vuelo
              type: 'FLIGHT',
              title: `✈️ Vuelo de ida: Destino ${match.estadio}`,
              date: fechaVuelo,
              location: `Aeropuerto Intl. cercano a ${match.estadio}`
            };

            // Evitar duplicados y añadir
            if (!this.myEvents.find(e => e.id === eventoPartido.id)) {
              this.myEvents.push(eventoPartido);
            }
            this.myEvents.push(eventoVuelo);
          });

          localStorage.setItem('myItinerary', JSON.stringify(this.myEvents));
          this.generateCalendar();
        }
        this.isDetailsModalOpen = false;
        this.processingId = null;
        this.closePackageDetails();
        alert(`¡Paquete ${pkg.name} adquirido! Se han generado vuelos de ida una semana antes de tus partidos.`);
      },
      error: () => {
        this.processingId = null;
        alert("Error en la compra.");
      }
    });
  }
  setTab(tab: 'itinerario' | 'paquetes') { this.activeTab = tab; }


  // ==========================================
  // 🌟 LÓGICA DEL CALENDARIO VISUAL
  // ==========================================
  generateCalendar() {
    this.calendarDays = [];
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();

    // Primer día del mes y total de días
    const firstDayIndex = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    // Rellenar espacios vacíos antes del primer día (para que encaje en la semana)
    for (let i = 0; i < firstDayIndex; i++) {
      this.calendarDays.push({ dayNum: null, dateStr: null, events: [] });
    }

    // Llenar los días reales del mes
    for (let i = 1; i <= daysInMonth; i++) {
      // Formato YYYY-MM-DD
      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;

      // Buscar si este día tiene eventos
      const dayEvents = this.myEvents.filter(e => e.date === dateStr);

      this.calendarDays.push({ dayNum: i, dateStr: dateStr, events: dayEvents });
    }
  }

  nextMonth() {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1);
    this.generateCalendar();
  }

  prevMonth() {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1);
    this.generateCalendar();
  }

  // ==========================================
  // LÓGICA DE EVENTOS
  // ==========================================
  addEvent() {
    if (!this.newEvent.title || !this.newEvent.date) return;
    this.myEvents.push({
      id: Date.now(),
      type: this.newEvent.type as 'FLIGHT' | 'HOTEL' | 'MATCH',
      title: this.newEvent.title,
      date: this.newEvent.date,
      location: this.newEvent.location || ''
    });
    this.myEvents.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    localStorage.setItem('myItinerary', JSON.stringify(this.myEvents));

    this.showAddEventForm = false;
    this.newEvent = { type: 'MATCH', title: '', date: '', location: '' };

    // 🌟 ACTUALIZAR VISTA
    this.generateCalendar();
  }

  deleteEvent(id: number) {
    this.myEvents = this.myEvents.filter(e => e.id !== id);
    localStorage.setItem('myItinerary', JSON.stringify(this.myEvents));
    // 🌟 ACTUALIZAR VISTA
    this.generateCalendar();
  }


}