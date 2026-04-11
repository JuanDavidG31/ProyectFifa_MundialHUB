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

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/football")
public class FootballController {

	@Autowired
	private FootballService footballService;
	@Autowired
	private UserRepository userRepo; 

	@GetMapping("/dashboard")
	public ResponseEntity<?> getDashboardData(@RequestParam String username) {
		try {
			User user = userRepo.findByUser(AESUtil.encrypt(username))
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			String userCountry = AESUtil.decrypt(user.getCoutry());

			String teamsData = footballService.getQualifiedTeams2026();
			String fixturesData = footballService.getTeamFixtures(userCountry);

			Map<String, Object> response = new HashMap<>();
			response.put("userCountry", userCountry);
			response.put("teamsData", teamsData);
			response.put("fixturesData", fixturesData);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error al procesar la solicitud"));
		}
	}

	@GetMapping("/worldcup2026")
	public ResponseEntity<?> getWorldCupData(@RequestParam String username) {
		try {
			User user = userRepo.findByUser(AESUtil.encrypt(username))
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			String apiResponse = footballService.getWorldCupTeams();

			Map<String, Object> response = new HashMap<>();
			response.put("userCountry", user.getCoutry()); 
			response.put("apiData", apiResponse);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error al procesar la solicitud"));
		}
	}

	@GetMapping("/leagues")
	public ResponseEntity<String> getLeagues() {
		return processResponse(footballService.getLeagues());
	}

	@GetMapping("/teams")
	public ResponseEntity<String> getTeams(@RequestParam String league, @RequestParam String season) {
		return processResponse(footballService.getTeams(league, season));
	}

	@GetMapping("/players")
	public ResponseEntity<String> getPlayers(@RequestParam String team, @RequestParam String season) {
		return processResponse(footballService.getPlayers(team, season));
	}

	private ResponseEntity<String> processResponse(String response) {
		if (response != null && !response.isEmpty()) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"Error al conectar con la API de Fútbol\"}");
		}
	}
}