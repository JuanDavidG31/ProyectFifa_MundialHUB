package co.edu.unbosque.projectFifaUbosque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.projectFifaUbosque.dto.FlightDTO;
import co.edu.unbosque.projectFifaUbosque.service.FlightService;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST diseñado para gestionar paquetes comerciales y consultas de
 * vuelos hacia las sedes oficiales.
 * <p>
 * Proporciona endpoints de integración logística orientados a planes de viaje
 * turístico del Mundial FIFA 2026.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {
	/** Servicio de orquestación y consumo de itinerarios de vuelo. */
	@Autowired
	private FlightService flightService;

	/**
	 * Consulta un catálogo dinámico y estructurado de paquetes de vuelo ajustados a
	 * un rango específico de fechas.
	 *
	 * @param username  Nombre del usuario interesado en cotizar.
	 * @param startDate Fecha de ida/salida requerida en formato cadena de texto.
	 * @param endDate   Fecha tope de retorno esperada.
	 * @return {@link ResponseEntity} envolviendo un Mapa con categorías y listados
	 *         de objetos de vuelo {@link FlightDTO}.
	 */
	@GetMapping("/package")
	public ResponseEntity<Map<String, List<FlightDTO>>> getPackage(@RequestParam String username,
			@RequestParam String startDate, @RequestParam String endDate) {

		Map<String, List<FlightDTO>> result = flightService.getFlightPackage(username, startDate, endDate);
		return ResponseEntity.ok(result);
	}
}