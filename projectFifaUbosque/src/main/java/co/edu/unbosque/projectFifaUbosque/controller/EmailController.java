package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.EmailDTO;
import co.edu.unbosque.projectFifaUbosque.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado del subsistema de notificaciones externas vía
 * Correo Electrónico.
 * <p>
 * Requiere tokens de autenticación válidos en cabecera HTTP bajo
 * especificaciones formales de OpenAPI.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class EmailController {
	/** Inyección del motor o servicio lógico de mensajería SMTP/Email. */
	@Autowired
	private EmailService emailService;

	/**
	 * Expone el endpoint público de envíos para despachar correos corporativos o
	 * informativos de la aplicación.
	 *
	 * @param request DTO que encapsula destinatario, asunto y el cuerpo del mensaje
	 *                en formato texto o HTML.
	 * @return {@link ResponseEntity} con texto plano indicando el estado definitivo
	 *         del envío.
	 */
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