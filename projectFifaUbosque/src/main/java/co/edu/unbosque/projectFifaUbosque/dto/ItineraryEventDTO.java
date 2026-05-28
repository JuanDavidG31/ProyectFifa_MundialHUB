package co.edu.unbosque.projectFifaUbosque.dto;

/**
 * Objeto de Transferencia de Datos (DTO) para la gestión de actividades en la
 * agenda o itinerario del usuario.
 * <p>
 * Representa un evento planificado por el usuario (partidos, vuelos, reservas
 * de hospedaje) durante su estancia en el mundial.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class ItineraryEventDTO {

	/** Identificador único del evento en la agenda en la base de datos. */
	private Long id;

	/** Cuenta de correo del usuario propietario del itinerario. */
	private String userEmail;

	/** Clasificación del evento (ej: Partido, Vuelo, Hotel). */
	private String eventType;

	/** Título o descripción corta del evento. */
	private String title;

	/** Fecha y hora programada para el evento en formato de texto. */
	private String eventDate;

	/**
	 * Ubicación geográfica, estadio o establecimiento donde se desarrollará la
	 * actividad.
	 */
	private String location;

	/**
	 * Constructor base por defecto.
	 */
	public ItineraryEventDTO() {
		// Constructor base vacío.
	}

	/** @return El ID del evento. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID a establecer. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El correo del usuario asociado. */
	public String getUserEmail() {
		return userEmail;
	}

	/** @param userEmail El nuevo correo de usuario a establecer. */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/** @return El tipo de evento. */
	public String getEventType() {
		return eventType;
	}

	/** @param eventType El nuevo tipo de evento a establecer. */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/** @return El título o nombre asignado a la actividad. */
	public String getTitle() {
		return title;
	}

	/** @param title El nuevo título a establecer. */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return La fecha programada del evento. */
	public String getEventDate() {
		return eventDate;
	}

	/** @param eventDate La nueva fecha de agenda a establecer. */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	/** @return El lugar de realización. */
	public String getLocation() {
		return location;
	}

	/** @param location La nueva ubicación a establecer. */
	public void setLocation(String location) {
		this.location = location;
	}
}