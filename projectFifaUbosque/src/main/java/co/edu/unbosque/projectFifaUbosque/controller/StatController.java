package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.PlayerStatDTO;
import co.edu.unbosque.projectFifaUbosque.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatController {

	@Autowired
	private MatchService matchService;

	@GetMapping("/scorers")
	public List<PlayerStatDTO> getScorers() {
		return matchService.getTopScorers();
	}

	@GetMapping("/assists")
	public List<PlayerStatDTO> getAssists() {
		return matchService.getTopAssists(); 
	}

	
}