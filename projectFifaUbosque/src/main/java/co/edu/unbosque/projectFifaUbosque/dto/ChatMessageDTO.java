package co.edu.unbosque.projectFifaUbosque.dto;

/**
 * DTO estructurado para la transferencia de mensajes en los canales de soporte
 * en tiempo real.
 * <p>
 * Mapea los payloads JSON intercambiados a través de WebSockets entre los
 * clientes y los agentes de soporte técnico.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class ChatMessageDTO {

	/**
	 * Enumerador con los tipos de mensajes y comandos del protocolo de soporte
	 * técnico.
	 */
	public enum MessageType {
		/** Mensaje estándar de conversación de texto entre partes. */
		CHAT,
		/** Comando del sistema para asignar un agente libre a un usuario en espera. */
		ASSIGN,
		/** Notificación de desconexión o finalización formal del chat. */
		DISCONNECT,
		/**
		 * Estado del sistema que indica al usuario que se encuentra en cola de espera.
		 */
		WAITING,
		/** Notificaciones automáticas emitidas por la plataforma. */
		SYSTEM
	}

	/** Tipo de mensaje según las acciones del protocolo de soporte. */
	private MessageType type;

	/** Identificador del remitente que envía el mensaje. */
	private String sender;

	/**
	 * Identificador del destinatario que debe recibir el mensaje en su cola
	 * privada.
	 */
	private String recipient;

	/** Contenido textual o cuerpo del mensaje de chat. */
	private String content;

	/**
	 * Constructor por defecto requerido para los procesos automáticos de
	 * deserialización JSON.
	 */
	public ChatMessageDTO() {
	}

	/**
	 * Constructor completo para la creación parametrizada de mensajes del chat de
	 * soporte.
	 *
	 * @param type      Tipo de mensaje del protocolo.
	 * @param sender    Remitente.
	 * @param recipient Destinatario.
	 * @param content   Cuerpo del mensaje.
	 */
	public ChatMessageDTO(MessageType type, String sender, String recipient, String content) {
		this.type = type;
		this.sender = sender;
		this.recipient = recipient;
		this.content = content;
	}

	/** @return El tipo de mensaje del chat. */
	public MessageType getType() {
		return type;
	}

	/** @param type El nuevo tipo de mensaje a establecer. */
	public void setType(MessageType type) {
		this.type = type;
	}

	/** @return El remitente del mensaje. */
	public String getSender() {
		return sender;
	}

	/** @param sender El nuevo remitente a establecer. */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/** @return El destinatario asignado. */
	public String getRecipient() {
		return recipient;
	}

	/** @param recipient El nuevo destinatario a establecer. */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/** @return El contenido o texto del mensaje. */
	public String getContent() {
		return content;
	}

	/** @param content El nuevo texto a establecer. */
	public void setContent(String content) {
		this.content = content;
	}
}