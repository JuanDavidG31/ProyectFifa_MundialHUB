package co.edu.unbosque.projectFifaUbosque.service;

import co.edu.unbosque.projectFifaUbosque.dto.TicketDTO;
import co.edu.unbosque.projectFifaUbosque.dto.UserDTO;
import co.edu.unbosque.projectFifaUbosque.model.Ticket;
import co.edu.unbosque.projectFifaUbosque.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
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
			System.err.println("Error al generar el QR localmente: " + e.getMessage());
		}

		Ticket ticket = new Ticket(ticketUuid, realEmail, matchName, stadium, matchDate, LocalDateTime.now());
		ticketRepository.save(ticket);

		boolean emailSent = emailService.sendTicketWithQR(realEmail, matchName, stadium, matchDate, ticketUuid,
				qrBase64);

		if (!emailSent) {
			System.err.println("El ticket se guardó pero el correo falló para: " + realEmail);
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
			RestTemplate restTemplate = new RestTemplate();
			String url = "https://veltrixpassqrgen.onrender.com/qr-" + uuid;

			@SuppressWarnings("unchecked")
			Map<String, Object> response = restTemplate.getForObject(url, Map.class);

			if (response != null && response.containsKey("qr_content")) {
				return (String) response.get("qr_content");
			}
		} catch (Exception e) {
			System.err.println("Error descargando contenido desde la API: " + e.getMessage());
		}
		return "";
	}
}