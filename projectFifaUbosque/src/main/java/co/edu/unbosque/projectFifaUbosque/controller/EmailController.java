package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.EmailDTO;
import co.edu.unbosque.projectFifaUbosque.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class EmailController {

	@Autowired
	private EmailService emailService;

	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(@RequestBody EmailDTO request) {
		boolean isSent = emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());

		if (isSent) {
			return ResponseEntity.ok("Correo enviado exitosamente.");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo.");
		}
	}

}