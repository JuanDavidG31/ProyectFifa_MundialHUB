package co.edu.unbosque.projectFifaUbosque.service;

import co.edu.unbosque.projectFifaUbosque.dto.TicketDTO;
import co.edu.unbosque.projectFifaUbosque.dto.UserDTO;
import co.edu.unbosque.projectFifaUbosque.model.Ticket;
import co.edu.unbosque.projectFifaUbosque.repository.TicketRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TicketService {

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserService userService;

	@Autowired
	private QRService qrService;

	@Autowired
	private ExternalHTTPRequestHandler httpHandler;

	public TicketDTO processTicketPurchase(String username, String matchName, String stadium, String matchDate) {

		long MAX_BOLETOS = 50;
		if (ticketRepository.countByMatchName(matchName) >= MAX_BOLETOS) {
			throw new RuntimeException("SOLD_OUT");
		}

		UserDTO user = userService.getByUser(username);

		if (user == null) {
			throw new RuntimeException("Usuario no encontrado en la base de datos: " + username);
		}

		String realEmail = user.getEmail();
		if (realEmail == null || realEmail.isBlank()) {
			realEmail = "sin_correo@mundialhub.com";
		}
		String ticketUuid = UUID.randomUUID().toString();

		String qrContent = obtenerContenidoQrDesdeApi(ticketUuid);

		if (qrContent == null || qrContent.isBlank()) {
			qrContent = "Ticket-UUID: " + ticketUuid;
		}

		String qrBase64 = "";
		try {
			qrBase64 = qrService.generateQRCodeBase64(qrContent);
		} catch (Exception e) {
		}

		Ticket ticket = new Ticket(ticketUuid, realEmail, matchName, stadium, matchDate, LocalDateTime.now());
		ticketRepository.save(ticket);

		boolean emailSent = emailService.sendTicketWithQR(realEmail, matchName, stadium, matchDate, ticketUuid,
				qrBase64);

		if (!emailSent) {
		}

		TicketDTO dto = new TicketDTO();
		dto.setUuid(ticketUuid);
		dto.setUserEmail(realEmail);
		dto.setMatchName(matchName);
		dto.setStadium(stadium);
		dto.setMatchDate(matchDate);
		dto.setPurchaseDate(ticket.getPurchaseDate());

		return dto;
	}

	private String obtenerContenidoQrDesdeApi(String uuid) {
		try {
			String jsonResponse = httpHandler.getFromQrApi("qr-" + uuid);

			if (jsonResponse != null && !jsonResponse.isBlank()) {
				JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
				if (root.has("qr_content") && !root.get("qr_content").isJsonNull()) {
					return root.get("qr_content").getAsString();
				}
			}
		} catch (Exception e) {
		}
		return "";
	}
}