package co.edu.unbosque.projectFifaUbosque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.projectFifaUbosque.dto.FlightDTO;
import co.edu.unbosque.projectFifaUbosque.service.FlightService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*") 
public class FlightController {

	@Autowired
	private FlightService flightService;

	
	@GetMapping("/package")
	public ResponseEntity<Map<String, List<FlightDTO>>> getPackage(
            @RequestParam String username,
			@RequestParam String startDate,
            @RequestParam String endDate) {
		

		Map<String, List<FlightDTO>> result = flightService.getFlightPackage(username, startDate, endDate);
		return ResponseEntity.ok(result);
	}
}