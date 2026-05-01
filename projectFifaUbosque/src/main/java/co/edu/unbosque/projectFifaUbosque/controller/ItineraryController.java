package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.ItineraryEventDTO;
import co.edu.unbosque.projectFifaUbosque.service.ItineraryEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
@CrossOrigin(origins = "*")
public class ItineraryController {

	@Autowired
	private ItineraryEventService service;

	// Traer los eventos del usuario
	@GetMapping("/{email}")
	public ResponseEntity<List<ItineraryEventDTO>> getUserItinerary(@PathVariable String email) {
		return ResponseEntity.ok(service.getUserItinerary(email));
	}

	// Guardar eventos masivamente (Paquetes o eventos individuales)
	@PostMapping("/save")
	public ResponseEntity<List<ItineraryEventDTO>> saveEvents(@RequestBody List<ItineraryEventDTO> events) {
		return ResponseEntity.ok(service.saveEvents(events));
	}

	// Borrar un evento
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
		service.deleteEvent(id);
		return ResponseEntity.ok().build();
	}
}