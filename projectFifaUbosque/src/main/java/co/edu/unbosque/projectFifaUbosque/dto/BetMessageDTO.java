package co.edu.unbosque.projectFifaUbosque.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) diseñado para estructurar la transmisión de
 * mensajes de pollas y apuestas.
 * <p>
 * Se utiliza para empaquetar payloads de comunicación interactiva asíncrona
 * sobre WebSockets (STOMP), transportando información de marcadores en salas de
 * apuestas, ids de partidos e identificadores de remitentes.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Data
public class BetMessageDTO {

	/**
	 * Identificador único de la sala de apuestas a la que va dirigida el mensaje.
	 */
	private String roomId;

	/** Username del cliente o emisor original del mensaje. */
	private String sender;

	/** Tipo o categoría de la acción del mensaje (ej: JOIN, LEAVE, SHARE). */
	private String type;

	/** Contenido textual opcional o cuerpo del mensaje enviado. */
	private String content;

	/** Identificador único del partido relacionado con la apuesta o predicción. */
	private Long matchId;

	/**
	 * Cadena descriptiva conteniendo información del encuentro (Ej: "Argentina vs
	 * Francia").
	 */
	private String matchInfo;

	/** Goles asignados por el usuario para el equipo local (Home Team). */
	private Integer homeScore;

	/** Goles asignados por el usuario para el equipo visitante (Away Team). */
	private Integer awayScore;

	/**
	 * Nombre del usuario objetivo a quien va dirigida una acción o reto específico
	 * dentro de la sala.
	 */
	private String targetUser;

	/**
	 * Constructor explícito por defecto.
	 */
	public BetMessageDTO() {
	}

	/** @return El nombre del usuario objetivo. */
	public String getTargetUser() {
		return targetUser;
	}

	/** @param targetUser El nuevo usuario objetivo a establecer. */
	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	/** @return El identificador de la sala de apuestas. */
	public String getRoomId() {
		return roomId;
	}

	/** @param roomId El nuevo identificador de la sala a establecer. */
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	/** @return El remitente del mensaje. */
	public String getSender() {
		return sender;
	}

	/** @param sender El nuevo remitente a establecer. */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/** @return El tipo de acción de mensajería. */
	public String getType() {
		return type;
	}

	/** @param type El nuevo tipo de acción a establecer. */
	public void setType(String type) {
		this.type = type;
	}

	/** @return El contenido del mensaje. */
	public String getContent() {
		return content;
	}

	/** @param content El nuevo contenido de texto a establecer. */
	public void setContent(String content) {
		this.content = content;
	}

	/** @return El identificador del partido. */
	public Long getMatchId() {
		return matchId;
	}

	/** @param matchId El nuevo identificador de partido a establecer. */
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	/** @return La descripción del encuentro deportivo. */
	public String getMatchInfo() {
		return matchInfo;
	}

	/** @param matchInfo La nueva descripción de partido a establecer. */
	public void setMatchInfo(String matchInfo) {
		this.matchInfo = matchInfo;
	}

	/** @return El marcador asignado al equipo local. */
	public Integer getHomeScore() {
		return homeScore;
	}

	/** @param homeScore La puntuación del equipo local. */
	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	/** @return El marcador asignado al equipo visitante. */
	public Integer getAwayScore() {
		return awayScore;
	}

	/** @param awayScore La puntuación del equipo visitante. */
	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}
}