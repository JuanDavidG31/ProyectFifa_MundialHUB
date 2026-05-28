package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de gestionar y exponer los datos de partidos en
 * vivo y del mundial.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {
	/** Servicio que administra las consultas de partidos de fútbol. */
	@Autowired
	private MatchService matchService;

	/**
	 * Obtiene el listado de partidos programados o jugados correspondientes a la
	 * Copa del Mundo (World Cup).
	 *
	 * @return {@link ResponseEntity} con la información de los encuentros
	 *         mundialistas.
	 */
	@GetMapping("/wc")
	public ResponseEntity<?> getWcMatches() {
		return ResponseEntity.ok(matchService.getWcMatches());
	}

	/**
	 * Obtiene todos los partidos de fútbol que se están disputando en tiempo real a
	 * nivel global.
	 *
	 * @return {@link ResponseEntity} con los partidos en vivo.
	 */
	@GetMapping("/all")
	public ResponseEntity<?> getAllLiveMatches() {
		return ResponseEntity.ok(matchService.getAllLiveMatches());
	}
}