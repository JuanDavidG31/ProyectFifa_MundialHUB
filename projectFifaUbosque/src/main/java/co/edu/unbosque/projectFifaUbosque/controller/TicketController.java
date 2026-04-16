package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.TicketDTO;
import co.edu.unbosque.projectFifaUbosque.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

	@Autowired
	private TicketService ticketService;

	@PostMapping("/buy")
	public ResponseEntity<?> buyTicket(@RequestBody Map<String, String> purchaseData) {
		try {
			String userEmail = purchaseData.get("userEmail");
			String matchName = purchaseData.get("matchName");
			String stadium = purchaseData.get("stadium");
			String date = purchaseData.get("date");

		
			TicketDTO savedTicket = ticketService.processTicketPurchase(userEmail, matchName, stadium, date);

			return ResponseEntity.ok(Map.of("message",
					"Compra exitosa. Tu ticket con código QR ha sido enviado al correo.", "ticket", savedTicket));

		} catch (Exception e) {
			if ("SOLD_OUT".equals(e.getMessage())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "Lo sentimos, los boletos para este partido se han agotado."));
			}

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error procesando la compra: " + e.getMessage()));
		}
	}
}