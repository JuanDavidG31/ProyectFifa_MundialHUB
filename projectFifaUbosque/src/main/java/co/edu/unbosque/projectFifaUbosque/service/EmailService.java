package co.edu.unbosque.projectFifaUbosque.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Base64;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	public boolean sendEmail(String toEmail, String subject, String body) {
		try {
			
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(fromEmail);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body, true);

			mailSender.send(message);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean sendTicketWithQR(String toEmail, String matchName, String stadium, String date, String ticketUuid,
			String qrBase64) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(toEmail);
			helper.setSubject("🎟️ ¡Tu entrada confirmada para " + matchName + "!");

			String htmlBody = "<div style='font-family: Arial, sans-serif; background-color: #0b0f19; color: #ffffff; padding: 30px; text-align: center; border-radius: 15px;'>"
					+ "<h2 style='color: #ec4899; margin-bottom: 5px;'>¡Compra Exitosa!</h2>"
					+ "<p style='color: #94a3b8; font-size: 16px;'>Has adquirido una entrada oficial.</p>"
					+ "<div style='background-color: rgba(255,255,255,0.05); padding: 20px; border-radius: 10px; margin: 20px auto; max-width: 400px; border: 1px solid rgba(255,255,255,0.1);'>"
					+ "<h3 style='margin: 0 0 10px 0;'>" + matchName + "</h3>"
					+ "<p style='margin: 5px 0; color: #06b6d4;'>📍 " + stadium + "</p>"
					+ "<p style='margin: 5px 0; color: #94a3b8;'>📅 " + date + "</p>" + "</div>"
					+ "<p>Presenta este código QR en la entrada del estadio:</p>"
					+ "<img src='cid:qrImage' alt='Código QR' style='width: 250px; height: 250px; border-radius: 15px; border: 3px solid #ec4899; margin-top: 15px;' />"
					+ "<p style='margin-top: 30px; font-size: 12px; color: #64748b;'>Ticket ID: " + ticketUuid + "</p>"
					+ "</div>";

			helper.setText(htmlBody, true);

			if (qrBase64.contains(",")) {
				qrBase64 = qrBase64.split(",")[1];
			}

			qrBase64 = qrBase64.replaceAll("\\s+", "");

			byte[] imageBytes = Base64.getDecoder().decode(qrBase64);
			ByteArrayResource imageResource = new ByteArrayResource(
					imageBytes);

			helper.addInline("qrImage", imageResource, "image/png");

			mailSender.send(message);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}