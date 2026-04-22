package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

	@Autowired
	private MatchService matchService;

	@GetMapping("/wc")
	public ResponseEntity<?> getWcMatches() {
		return ResponseEntity.ok(matchService.getWcMatches());
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllLiveMatches() {
		return ResponseEntity.ok(matchService.getAllLiveMatches());
	}
}