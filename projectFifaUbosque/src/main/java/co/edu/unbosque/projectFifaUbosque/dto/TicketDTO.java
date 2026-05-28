package co.edu.unbosque.projectFifaUbosque.dto;

import java.time.LocalDateTime;

/**
 * Objeto de Transferencia de Datos (DTO) para la representación de las Entradas
 * o Tickets digitales vendidos.
 * <p>
 * Se utiliza para transportar la información consolidada de un boleto comprado
 * por un usuario para asistir a un partido específico de la Copa del Mundo,
 * incluyendo el código único global (UUID) de control de acceso.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class TicketDTO {

	/**
	 * Código alfanumérico global único (UUID) utilizado para la posterior
	 * validación o control de aforo.
	 */
	private String uuid;

	/**
	 * Correo electrónico institucional o personal del usuario propietario del
	 * boleto.
	 */
	private String userEmail;

	/**
	 * Descripción o nombre del encuentro deportivo asignado (ej: "Francia vs
	 * Colombia").
	 */
	private String matchName;

	/** Nombre del estadio oficial sede donde se disputará el partido. */
	private String stadium;

	/** Programación horaria y fecha oficial establecida para el evento. */
	private String matchDate;

	/**
	 * Marca temporal (Fecha y Hora) exacta en la cual el usuario realizó el pago de
	 * la entrada.
	 */
	private LocalDateTime purchaseDate;

	/**
	 * Constructor por defecto requerido para los procesos automáticos de
	 * serialización y mapeo relacional.
	 */
	public TicketDTO() {
	}

	/**
	 * @return El código de identificación global único UUID de la entrada.
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid El nuevo código de validación UUID a establecer.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return El correo electrónico del usuario adquirente.
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail El nuevo correo electrónico de usuario a configurar.
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @return La descripción del partido asignado.
	 */
	public String getMatchName() {
		return matchName;
	}

	/**
	 * @param matchName El nuevo nombre o descripción de partido a establecer.
	 */
	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	/**
	 * @return El nombre del estadio sede del encuentro.
	 */
	public String getStadium() {
		return stadium;
	}

	/**
	 * @param stadium El nuevo estadio a asignar al boleto.
	 */
	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	/**
	 * @return La fecha y hora programada del partido.
	 */
	public String getMatchDate() {
		return matchDate;
	}

	/**
	 * @param matchDate La nueva fecha de programación deportiva a configurar.
	 */
	public void setMatchDate(String matchDate) {
		this.matchDate = matchDate;
	}

	/**
	 * @return La marca temporal de la transacción de compra.
	 */
	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}

	/**
	 * @param purchaseDate La nueva fecha y hora de compra a asociar en el registro.
	 */
	public void setPurchaseDate(LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
}