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

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.cargarPartidosReales();
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