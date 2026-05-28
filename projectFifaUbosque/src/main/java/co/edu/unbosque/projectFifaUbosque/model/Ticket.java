package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de persistencia que representa un Boleto o Entrada digital para un
 * partido de fútbol.
 * <p>
 * Registra y controla la metadata legal de la compra de entradas a los estadios
 * oficiales, asociando un código único UUID de validación para control de
 * aforo.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "tickets")
public class Ticket {

	/** Identificador numérico de la entrada en base de datos. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Código alfanumérico global único (UUID) utilizado para la generación y
	 * validación del código QR de acceso.
	 */
	@Column(unique = true, nullable = false)
	private String uuid;

	/**
	 * Correo electrónico institucional o personal del usuario adquirente de la
	 * boleta.
	 */
	@Column(nullable = false)
	private String userEmail;

	/** Nombre o identificador del partido asignado (ej: "Francia vs Colombia"). */
	@Column(nullable = false)
	private String matchName;

	/** Nombre del estadio oficial donde se disputará el encuentro deportivo. */
	private String stadium;

	/** Programación horaria y fecha oficial establecida para el partido. */
	private String matchDate;

	/**
	 * Marca temporal (Fecha y Hora) exacta en la cual el usuario realizó la
	 * transacción de pago de la entrada.
	 */
	@Column(name = "purchase_date")
	private LocalDateTime purchaseDate;

	/**
	 * Constructor por defecto para cumplir con el estándar de ciclo de vida de
	 * Hibernate.
	 */
	public Ticket() {
	}

	/**
	 * Constructor completo parametrizado para la instanciación de entradas
	 * digitales compradas.
	 *
	 * @param uuid         Identificador global único del boleto.
	 * @param userEmail    Email del comprador.
	 * @param matchName    Descripción del partido.
	 * @param stadium      Estadio asignado.
	 * @param matchDate    Fecha del partido.
	 * @param purchaseDate Fecha y hora de compra.
	 */
	public Ticket(String uuid, String userEmail, String matchName, String stadium, String matchDate,
			LocalDateTime purchaseDate) {
		this.uuid = uuid;
		this.userEmail = userEmail;
		this.matchName = matchName;
		this.stadium = stadium;
		this.matchDate = matchDate;
		this.purchaseDate = purchaseDate;
	}

	/** @return ID numérico del boleto. */
	public Long getId() {
		return id;
	}

	/** @param id Nuevo ID de base de datos a establecer. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El identificador global único UUID. */
	public String getUuid() {
		return uuid;
	}

	/** @param uuid El nuevo UUID de validación QR a configurar. */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/** @return El correo del adquirente. */
	public String getUserEmail() {
		return userEmail;
	}

	/** @param userEmail El nuevo correo de usuario a configurar. */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/** @return La descripción del encuentro deportivo. */
	public String getMatchName() {
		return matchName;
	}

	/** @param matchName La nueva descripción de partido a asignar. */
	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	/** @return El nombre del estadio oficial. */
	public String getStadium() {
		return stadium;
	}

	/** @param stadium El nuevo estadio oficial a configurar. */
	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	/** @return La fecha programada del partido. */
	public String getMatchDate() {
		return matchDate;
	}

	/** @param matchDate La nueva fecha de partido a establecer. */
	public void setMatchDate(String matchDate) {
		this.matchDate = matchDate;
	}

	/** @return La fecha y hora de la transacción financiera. */
	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}

	/** @param purchaseDate La nueva fecha de compra a registrar. */
	public void setPurchaseDate(LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
}