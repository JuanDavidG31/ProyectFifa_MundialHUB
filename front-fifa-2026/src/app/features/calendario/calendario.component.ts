import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { TicketsService } from '../../core/services/ticket.service';
import { MatchesService } from '../../core/services/matches.service';
import { ItineraryService } from '../../core/services/itinerary.service';
import { FlightsService } from '../../core/services/flights.service';
import { ReportsService } from '../../core/services/reports.service';
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
  category: string; 
  description: string;
  price: number;
  imageIcon: string;
  includedMatches: string;
  matchesRawData?: any[]; 
}

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
  searchTerm: string = ''; 
  packagesRaw: TravelPackage[] = []; 
  selectedPackage: any = null;
  isDetailsModalOpen = false;
  allMatches: any[] = [];
  activeTab: string = 'itinerario';
  processingId: number | null = null;
  selectedEvent: ItineraryEvent | null = null;
  isEventModalOpen = false;
  myEvents: ItineraryEvent[] = [];
  showAddEventForm = false;
  newEvent: Partial<ItineraryEvent> = { type: 'MATCH', title: '', date: '', location: '' };
  selectedMatchIds: number[] = [];  
  packages: any[] = [];
  newEventTitle: string = '';
  newEventDate: string = '';
  newEventType: 'FLIGHT' | 'HOTEL' | 'MATCH' | 'OTHER' = 'OTHER';
  newEventLocation: string = '';
  isGeneratingPack: boolean = false;
  isSavingManual: boolean = false;
  userEventsGrouped: any[] = [];
  currentDate: Date = new Date();
  weekDays: string[] = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  calendarDays: CalendarDay[] = [];
  userReports: any[] = [];
  isLoadingReports: boolean = false;
  constructor(private authService: AuthService, private ticketsService: TicketsService, private matchesService: MatchesService, private itineraryService: ItineraryService, private flightsService: FlightsService, private reportsService: ReportsService) { }

  ngOnInit(): void {
    this.cargarDatosIniciales();
  }

  toggleMatchSelection(matchId: number) {
    const index = this.selectedMatchIds.indexOf(matchId);
    if (index > -1) {
      this.selectedMatchIds.splice(index, 1); 
    } else {
      this.selectedMatchIds.push(matchId); 
    }
  }


  createCustomPackage() {
    if (this.selectedMatchIds.length === 0) return;
    this.isGeneratingPack = true;
    const currentUser = localStorage.getItem('username') || '';

    const fechasTimestamp = this.selectedMatchIds.map(id => {
      const m = this.allMatches.find(x => x.id == id);
      return new Date(this.formatearFechaParaCalendario(m.fecha) + 'T00:00:00').getTime();
    });

    const minDate = new Date(Math.min(...fechasTimestamp));
    const maxDate = new Date(Math.max(...fechasTimestamp));

    const startDateBackend = minDate.toISOString().split('T')[0];
    const endDateBackend = maxDate.toISOString().split('T')[0];

    this.flightsService.getFlightPackage(currentUser, startDateBackend, endDateBackend).subscribe({
      next: (vuelos) => {
        if (!vuelos || !vuelos.outbound || vuelos.outbound.length === 0) {
          alert("⚠️ Google Flights no encontró rutas. Intenta con otros partidos.");
          this.isGeneratingPack = false;
          return;
        }

        const outboundFlight = vuelos.outbound[0];

        let inboundFlight = null;
        if (vuelos.inbound && vuelos.inbound.length > 0) {
          inboundFlight = vuelos.inbound[0];
        } else {
          const returnDateObj = new Date(endDateBackend + 'T00:00:00');
          returnDateObj.setDate(returnDateObj.getDate() + 2);
          inboundFlight = { airline: outboundFlight.airline, price: outboundFlight.price, departureTime: returnDateObj.toISOString().split('T')[0] + " 10:00" };
        }

        const eventsToSave: any[] = [];
        eventsToSave.push({ userEmail: currentUser, eventType: 'FLIGHT', title: `✈︎ Vuelo Ida: ${outboundFlight.airline}`, eventDate: outboundFlight.departureTime.split(' ')[0], location: `Aprox: $${outboundFlight.price} USD` });

        this.selectedMatchIds.forEach(id => {
          const match = this.allMatches.find(m => m.id == id);
          eventsToSave.push({ userEmail: currentUser, eventType: 'MATCH', title: `Partido: ${match.local} vs ${match.visitante}`, eventDate: this.formatearFechaParaCalendario(match.fecha), location: match.estadio });
        });

        eventsToSave.push({ userEmail: currentUser, eventType: 'FLIGHT', title: `✈︎ Vuelo Regreso: ${inboundFlight.airline}`, eventDate: inboundFlight.departureTime.split(' ')[0], location: `Aprox: $${inboundFlight.price} USD` });

        this.itineraryService.saveItineraryEvents(eventsToSave).subscribe({
          next: () => {
            const report = { userEmail: currentUser, packageName: `Itinerario Inteligente (${this.selectedMatchIds.length} partidos)`, packageType: 'GENERADO' };
            this.reportsService.savePackageReport(report).subscribe();
            this.isGeneratingPack = false;
            alert("¡Itinerario Épico Generado exitosamente!");
            this.selectedMatchIds = [];
            this.cargarDatosIniciales();
          },
          error: () => { this.isGeneratingPack = false; alert("Error guardando el itinerario."); }
        });
      },
      error: () => { this.isGeneratingPack = false; alert("No hubo conexión con el backend."); }
    });
  }

  openEventModal(ev: ItineraryEvent) {
    this.selectedEvent = ev;
    this.isEventModalOpen = true;
  }

  closeEventModal() {
    this.selectedEvent = null;
    this.isEventModalOpen = false;
  }

  openPackageDetails(pkg: any) {
    this.selectedPackage = pkg;
    this.isDetailsModalOpen = true;
    window.scrollTo(0, 0);
  }

  closePackageDetails() {
    this.selectedPackage = null;
    this.isDetailsModalOpen = false;
  }

  generarFechaVuelo(fechaPartidoStr: string): string {
    const date = new Date(fechaPartidoStr + 'T00:00:00'); 
    date.setDate(date.getDate() - 7); 
    return date.toISOString().split('T')[0];
  }

  cargarDatosIniciales() {
    const currentUser = localStorage.getItem('username') || 'usuario';

    this.itineraryService.getUserItinerary(currentUser).subscribe({
      next: (eventosGuardados) => {
        this.myEvents = eventosGuardados.map(e => ({
          id: e.id,
          type: e.eventType,
          title: e.title,
          date: e.eventDate,
          location: e.location
        }));
        this.generateCalendar();
      },
      error: (err) => console.error("Error cargando el itinerario:", err)
    });

    this.matchesService.getWcMatches().subscribe({
      next: (data) => {
        this.allMatches = data;
        this.generarCatalogoDePaquetes();
      },
      error: (err) => console.error("❌ Error al traer partidos:", err)
    });
  }
  cerrarSesion() {
    this.authService.logout();
  }

  generarCatalogoDePaquetes() {
    const nuevosPaquetes: TravelPackage[] = [];

    if (!this.allMatches || this.allMatches.length === 0) return;

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
        price: partidosPais.length * 150, 
        imageIcon: "✉", 
        includedMatches: partidosPais.map(m => `${m.local} vs ${m.visitante}`).join(', '),
        matchesRawData: partidosPais
      });
    });

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
        imageIcon: "✉", 
        includedMatches: `${partidosGrupo.length} partidos de la fase inicial.`,
        matchesRawData: partidosGrupo
      });
    });


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
        imageIcon: "VIP", 
        includedMatches: "Partidos de eliminación directa y Final.",
        matchesRawData: faseFinal
      });
    }

    this.packages = nuevosPaquetes;
    this.packagesRaw = [...this.packages];
  }
  filterPackages() {
    if (!this.searchTerm) {
      this.packages = [...this.packagesRaw];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.packages = this.packagesRaw.filter(p =>
        p.name.toLowerCase().includes(term) ||
        p.description.toLowerCase().includes(term) ||
        p.category.toLowerCase().includes(term)
      );
    }
  }
  formatearFechaParaCalendario(fechaRaw: string): string {
    if (!fechaRaw) return '';

    const meses: { [key: string]: string } = {
      'ene': '01', 'feb': '02', 'mar': '03', 'abr': '04', 'may': '05', 'jun': '06',
      'jul': '07', 'ago': '08', 'sep': '09', 'oct': '10', 'nov': '11', 'dic': '12'
    };

    try {
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

    const purchasePayload = { userEmail: currentUser, matchName: `PAQUETE: ${pkg.name}`, stadium: 'Sedes Varias', date: 'Múltiples Fechas' };

    this.ticketsService.buyTicket(purchasePayload).subscribe({
      next: () => {
        if (pkg.matchesRawData && pkg.matchesRawData.length > 0) {

          const fechasTimestamp = pkg.matchesRawData.map((m: any) =>
            new Date(this.formatearFechaParaCalendario(m.fecha) + 'T00:00:00').getTime()
          );

          const minDate = new Date(Math.min(...fechasTimestamp));
          const maxDate = new Date(Math.max(...fechasTimestamp));

          const startDateBackend = minDate.toISOString().split('T')[0];
          const endDateBackend = maxDate.toISOString().split('T')[0];

          this.flightsService.getFlightPackage(currentUser, startDateBackend, endDateBackend).subscribe({
            next: (vuelos) => {
              const eventosAGuardar: any[] = [];
              let outboundFlight = null;
              let inboundFlight = null;

              if (vuelos && vuelos.outbound && vuelos.outbound.length > 0) {
                outboundFlight = vuelos.outbound[0];
                inboundFlight = (vuelos.inbound && vuelos.inbound.length > 0) ? vuelos.inbound[0] : outboundFlight;

                eventosAGuardar.push({ userEmail: currentUser, eventType: 'FLIGHT', title: `✈︎ Vuelo Ida: ${outboundFlight.airline}`, eventDate: outboundFlight.departureTime.split(' ')[0], location: `Costo aprox: $${outboundFlight.price} USD` });
              } else {
                eventosAGuardar.push({ userEmail: currentUser, eventType: 'FLIGHT', title: `✈︎ Vuelo Ida (Sugerido)`, eventDate: this.generarFechaVuelo(startDateBackend), location: `Hacia Sede del Mundial` });
              }

              pkg.matchesRawData.forEach((match: any) => {
                eventosAGuardar.push({ userEmail: currentUser, eventType: 'MATCH', title: `${match.local} vs ${match.visitante}`, eventDate: this.formatearFechaParaCalendario(match.fecha), location: match.estadio });
              });

              if (inboundFlight && outboundFlight) {
                const returnDate = vuelos.inbound && vuelos.inbound.length > 0 ? inboundFlight.departureTime.split(' ')[0] : (() => {
                  const d = new Date(endDateBackend + 'T00:00:00');
                  d.setDate(d.getDate() + 2);
                  return d.toISOString().split('T')[0];
                })();

                eventosAGuardar.push({ userEmail: currentUser, eventType: 'FLIGHT', title: `✈︎ Vuelo Regreso: ${inboundFlight.airline}`, eventDate: returnDate, location: `Costo aprox: $${inboundFlight.price} USD` });
              }

              this.itineraryService.saveItineraryEvents(eventosAGuardar).subscribe({
                next: () => {
                  const report = { userEmail: currentUser, packageName: pkg.name, packageType: 'PREDEFINIDO' };
                  this.reportsService.savePackageReport(report).subscribe();
                  this.cargarDatosIniciales();
                  this.isDetailsModalOpen = false;
                  this.processingId = null;
                  this.closePackageDetails();
                  alert(`¡Paquete ${pkg.name} adquirido y organizado correctamente en tu calendario!`);
                },
                error: () => { alert("Hubo un error armando el itinerario."); this.processingId = null; }
              });
            },
            error: () => { alert("No pudimos conectar con la aerolínea."); this.processingId = null; }
          });
        }
      },
      error: () => { this.processingId = null; alert("Error en la compra."); }
    });
  }
  setTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'reportes') {
      this.loadReports();
    }
  }
  loadReports() {
    const currentUser = localStorage.getItem('username') || '';
    if (!currentUser) return;

    this.isLoadingReports = true;
    this.reportsService.getUserReports(currentUser).subscribe({
      next: (data) => {
        this.userReports = data;
        this.isLoadingReports = false;
      },
      error: (err) => {
        console.error('Error cargando reportes', err);
        this.isLoadingReports = false;
      }
    });
  }



  generateCalendar() {
    this.calendarDays = [];
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();

    const firstDayIndex = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    for (let i = 0; i < firstDayIndex; i++) {
      this.calendarDays.push({ dayNum: null, dateStr: null, events: [] });
    }

    for (let i = 1; i <= daysInMonth; i++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;

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


  saveManualEvent() {
    if (!this.newEventTitle || !this.newEventDate) {
      alert("⚠️ Por favor, ingresa al menos el título y la fecha del evento.");
      return;
    }

    this.isSavingManual = true; 

    const currentUser = localStorage.getItem('username') || 'usuario';
    const eventToSave = [{
      userEmail: currentUser, eventType: this.newEventType, title: this.newEventTitle, eventDate: this.newEventDate, location: this.newEventLocation || ''
    }];

    this.itineraryService.saveItineraryEvents(eventToSave).subscribe({
      next: () => {
        const report = { userEmail: currentUser, packageName: `Evento: ${this.newEventTitle}`, packageType: 'MANUAL' };
        this.reportsService.savePackageReport(report).subscribe();
        this.isSavingManual = false;
        this.showAddEventForm = false;
        this.newEventTitle = ''; this.newEventDate = ''; this.newEventLocation = ''; this.newEventType = 'OTHER';
        this.cargarDatosIniciales();
      },
      error: (err) => {
        this.isSavingManual = false; 
        console.error("Error añadiendo evento individual", err);
        alert("Hubo un error al guardar tu evento.");
      }
    });
  }

  deleteEvent(id: number) {
    this.itineraryService.deleteItineraryEvent(id).subscribe({
      next: () => {
        this.myEvents = this.myEvents.filter(e => e.id !== id);
        this.generateCalendar();
      },
      error: (err) => console.error("Error al borrar evento", err)
    });
  }


}