package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.PlayerStatDTO;
import co.edu.unbosque.projectFifaUbosque.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de proveer las estadísticas de rendimiento de los
 * jugadores en el torneo.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatController {
	/**
	 * Servicio de partidos de donde se extraen las métricas globales del torneo.
	 */
	@Autowired
	private MatchService matchService;

	/**
	 * Obtiene el listado de los máximos goleadores (Top Scorers) registrados en la
	 * competición.
	 *
	 * @return Lista de {@link PlayerStatDTO} con las métricas de goles por jugador.
	 */
	@GetMapping("/scorers")
	public List<PlayerStatDTO> getScorers() {
		return matchService.getTopScorers();
	}

	/**
	 * Obtiene el listado de los máximos asistentes (Top Assists) registrados en la
	 * competición.
	 *
	 * @return Lista de {@link PlayerStatDTO} con las métricas de asistencias por
	 *         jugador.
	 */
	@GetMapping("/assists")
	public List<PlayerStatDTO> getAssists() {
		return matchService.getTopAssists();
	}

}