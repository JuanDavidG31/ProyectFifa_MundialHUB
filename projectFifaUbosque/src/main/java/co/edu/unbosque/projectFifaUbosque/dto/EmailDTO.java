package co.edu.unbosque.projectFifaUbosque.dto;

/**
 * Objeto de Transferencia de Datos (DTO) para la parametrización de correos
 * electrónicos.
 * <p>
 * Transporta los datos esenciales hacia el servicio SMTP para el envío de
 * códigos OTP, confirmaciones de compras de boletería o alertas del sistema.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class EmailDTO {

	/** Dirección de correo electrónico del destinatario (To). */
	private String to;

	/** Asunto o encabezado del correo electrónico (Subject). */
	private String subject;

	/** Cuerpo del mensaje, admite texto plano o estructuras de maquetación HTML. */
	private String body;

	/**
	 * Constructor por defecto para la inicialización de la entidad de mensajería.
	 */
	public EmailDTO() {
		// Constructor base vacío.
	}

	/** @return El correo del destinatario. */
	public String getTo() {
		return to;
	}

	/** @param to El nuevo correo de destino a configurar. */
	public void setTo(String to) {
		this.to = to;
	}

	/** @return El asunto del correo. */
	public String getSubject() {
		return subject;
	}

	/** @param subject El nuevo asunto a configurar. */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/** @return El contenido o cuerpo del correo. */
	public String getBody() {
		return body;
	}

	/** @param body El nuevo cuerpo a configurar. */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Genera una representación en formato de texto con los campos del DTO de
	 * email.
	 *
	 * @return Cadena de texto de EmailDTO.
	 */
	@Override
	public String toString() {
		return "EmailDTO [to=" + to + ", subject=" + subject + ", body=" + body + "]";
	}
}