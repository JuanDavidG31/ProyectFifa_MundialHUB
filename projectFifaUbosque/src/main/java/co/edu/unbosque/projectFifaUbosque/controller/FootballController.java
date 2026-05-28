package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.model.User;
import co.edu.unbosque.projectFifaUbosque.repository.UserRepository;
import co.edu.unbosque.projectFifaUbosque.service.FootballService;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST encargado de centralizar y proveer la información
 * futbolística del sistema.
 * <p>
 * Gestiona el aprovisionamiento de datos para el Dashboard deportivo del
 * Mundial 2026, consultas de equipos clasificados, fixture personalizado por
 * país, ligas, equipos y jugadores.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/football")
public class FootballController {
	/** Servicio encargado de la lógica y consumo de APIs externas de fútbol. */
	@Autowired
	private FootballService footballService;
	/** Repositorio para la consulta y verificación de persistencia de usuarios. */
	@Autowired
	private UserRepository userRepo;

	/**
	 * Recupera y consolida los datos requeridos para renderizar el Dashboard
	 * principal del usuario.
	 * <p>
	 * Busca al usuario por su nombre cifrado, descifra su país de origen y obtiene
	 * el fixture correspondiente junto con las selecciones clasificadas al Mundial
	 * 2026.
	 * </p>
	 *
	 * @param username Nombre del usuario que solicita los datos de su dashboard.
	 * @return {@link ResponseEntity} con el mapa de datos (teams, fixtures,
	 *         userCountry) o código 500 en caso de falla.
	 */
	@GetMapping("/dashboard")
	public ResponseEntity<?> getDashboardData(@RequestParam String username) {
		try {
			User user = userRepo.findByUser(AESUtil.encrypt(username))
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			String userCountry = AESUtil.decrypt(user.getCoutry());

			String teamsData = footballService.getQualifiedTeams2026();
			String fixturesData = footballService.getTeamFixtures(userCountry);

			Map<String, Object> response = new HashMap<>();
			response.put("teams", teamsData);
			response.put("fixtures", fixturesData);
			response.put("userCountry", userCountry);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error al procesar la solicitud del Dashboard"));
		}
	}

	/**
	 * Obtiene los datos generales de las selecciones de la Copa del Mundo junto con
	 * el país de origen del usuario descifrado.
	 *
	 * @param username Nombre del usuario que consulta.
	 * @return {@link ResponseEntity} con el país y los datos de los equipos de la
	 *         Copa del Mundo, o código 500 en caso de error.
	 */
	@GetMapping("/worldcup")
	public ResponseEntity<?> getWorldCupData(@RequestParam String username) {
		try {
			User user = userRepo.findByUser(AESUtil.encrypt(username))
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			String apiResponse = footballService.getWorldCupTeams();

			Map<String, Object> response = new HashMap<>();
			response.put("userCountry", AESUtil.decrypt(user.getCoutry()));
			response.put("apiData", apiResponse);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error al procesar la solicitud"));
		}
	}

	/**
	 * Recupera el listado general de ligas de fútbol disponibles en el sistema.
	 *
	 * @return {@link ResponseEntity} con la cadena JSON de las ligas o HTTP 244 No
	 *         Content si está vacío.
	 */
	@GetMapping("/leagues")
	public ResponseEntity<String> getLeagues() {
		return processResponse(footballService.getLeagues());
	}

	/**
	 * Obtiene el listado de equipos pertenecientes a una liga y temporada
	 * específicas.
	 *
	 * @param league Identificador o nombre de la liga.
	 * @param season Año o identificador de la temporada.
	 * @return {@link ResponseEntity} con los datos de los equipos encontrados o sin
	 *         contenido.
	 */
	@GetMapping("/teams")
	public ResponseEntity<String> getTeams(@RequestParam String league, @RequestParam String season) {
		return processResponse(footballService.getTeams(league, season));
	}

	/**
	 * Obtiene la plantilla de jugadores de un equipo específico para una
	 * determinada temporada.
	 *
	 * @param team   Identificador o nombre del equipo.
	 * @param season Año de la temporada comercial o deportiva.
	 * @return {@link ResponseEntity} con la información de los jugadores o mensaje
	 *         estándar de ausencia de datos.
	 */
	@GetMapping("/players")
	public ResponseEntity<String> getPlayers(@RequestParam String team, @RequestParam String season) {
		return processResponse(footballService.getPlayers(team, season));
	}

	/**
	 * Método utilitario privado para validar y procesar de manera estandarizada las
	 * respuestas del servicio de fútbol.
	 * <p>
	 * Si la respuesta es nula, vacía o coincide con una estructura vacía de la API
	 * externa ({@code {"response":[]}}), retorna un estado HTTP 204 No Content con
	 * un cuerpo informativo.
	 * </p>
	 *
	 * @param response Cadena JSON cruda devuelta por la capa de servicio.
	 * @return {@link ResponseEntity} configurada según el estado y contenido de los
	 *         datos.
	 */
	private ResponseEntity<String> processResponse(String response) {
		if (response != null && !response.isEmpty() && !response.equals("{\"response\":[]}")) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\": \"No se encontraron datos\"}");
		}
	}
}