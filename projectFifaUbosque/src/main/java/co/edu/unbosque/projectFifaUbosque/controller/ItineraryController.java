package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.ItineraryEventDTO;
import co.edu.unbosque.projectFifaUbosque.service.ItineraryEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar la agenda o itinerario personal de
 * eventos de los usuarios.
 * <p>
 * Permite a los hinchas planificar y almacenar sus cronogramas de actividades,
 * partidos a asistir y reservas.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/itinerary")
@CrossOrigin(origins = "*")
public class ItineraryController {
	/**
	 * Servicio lógico encargado del procesamiento de agendas y eventos del
	 * itinerario.
	 */
	@Autowired
	private ItineraryEventService service;

	/**
	 * Obtiene el itinerario completo de eventos vinculados al correo electrónico de
	 * un usuario.
	 *
	 * @param email Correo electrónico identificador del usuario.
	 * @return {@link ResponseEntity} conteniendo la lista de objetos
	 *         {@link ItineraryEventDTO}.
	 */
	@GetMapping("/{email}")
	public ResponseEntity<List<ItineraryEventDTO>> getUserItinerary(@PathVariable String email) {
		return ResponseEntity.ok(service.getUserItinerary(email));
	}

	/**
	 * Guarda o actualiza un lote (lista) de eventos en la agenda del usuario.
	 *
	 * @param events Lista de objetos {@link ItineraryEventDTO} a persistir.
	 * @return {@link ResponseEntity} con la lista de eventos guardados con sus
	 *         respectivos identificadores.
	 */
	@PostMapping("/save")
	public ResponseEntity<List<ItineraryEventDTO>> saveEvents(@RequestBody List<ItineraryEventDTO> events) {
		return ResponseEntity.ok(service.saveEvents(events));
	}

	/**
	 * Elimina un evento específico del itinerario del usuario mediante su ID único.
	 *
	 * @param id Identificador único del evento a suprimir.
	 * @return {@link ResponseEntity} con estado HTTP 200 OK tras completar la
	 *         operación.
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
		service.deleteEvent(id);
		return ResponseEntity.ok().build();
	}
}