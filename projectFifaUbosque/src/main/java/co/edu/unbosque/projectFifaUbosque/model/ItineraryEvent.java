package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;

/**
 * Entidad de persistencia que mapea un Evento o Actividad integrada en el
 * itinerario de viaje de un hincha.
 * <p>
 * Permite almacenar las planificaciones turísticas y de asistencia a partidos
 * de los usuarios, estructurando su agenda personal para la Copa del Mundo.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "itinerary_events")
public class ItineraryEvent {

	/** Identificador único autoincremental de la actividad de agenda. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Correo electrónico del usuario dueño y planificador de esta actividad de
	 * viaje.
	 */
	@Column(nullable = false)
	private String userEmail;

	/**
	 * Categoría o naturaleza del suceso agendado (ej: Partido, Vuelo, Reserva
	 * Hotelera).
	 */
	@Column(nullable = false)
	private String eventType;

	/**
	 * Título explicativo o nombre personalizado asignado a la actividad en la
	 * agenda.
	 */
	@Column(nullable = false)
	private String title;

	/**
	 * Formato textual que registra la fecha y hora programada para el inicio del
	 * evento.
	 */
	@Column(nullable = false)
	private String eventDate;

	/**
	 * Lugar, establecimiento comercial o estadio donde se desarrollará la actividad
	 * agendada.
	 */
	private String location;

	/**
	 * Constructor por defecto requerido para procesos de mapeo relacional de
	 * Hibernate.
	 */
	public ItineraryEvent() {
	}

	/**
	 * Constructor con todos los campos lógicos para la creación manual de
	 * actividades de agenda.
	 *
	 * @param userEmail Email del propietario.
	 * @param eventType Tipo de actividad.
	 * @param title     Título o nombre.
	 * @param eventDate Fecha y hora programada.
	 * @param location  Lugar físico o establecimiento.
	 */
	public ItineraryEvent(String userEmail, String eventType, String title, String eventDate, String location) {
		this.userEmail = userEmail;
		this.eventType = eventType;
		this.title = title;
		this.eventDate = eventDate;
		this.location = location;
	}

	/** @return Identificador secuencial del evento. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de persistencia a asignar. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El correo del usuario propietario. */
	public String getUserEmail() {
		return userEmail;
	}

	/** @param userEmail El nuevo correo del propietario del itinerario. */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/** @return La categoría de la actividad agendada. */
	public String getEventType() {
		return eventType;
	}

	/** @param eventType El nuevo tipo o categoría de evento. */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/** @return El título o descripción corta de la actividad. */
	public String getTitle() {
		return title;
	}

	/** @param title El nuevo título a asignar en agenda. */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return La fecha y hora de la agenda. */
	public String getEventDate() {
		return eventDate;
	}

	/** @param eventDate La nueva fecha de programación. */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	/** @return El lugar geográfico o de establecimiento. */
	public String getLocation() {
		return location;
	}

	/** @param location La nueva ubicación física a configurar. */
	public void setLocation(String location) {
		this.location = location;
	}
}