package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;

@Entity
@Table(name = "itinerary_events")
public class ItineraryEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String userEmail;

	@Column(nullable = false)
	private String eventType; // FLIGHT, HOTEL, MATCH

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String eventDate; // Lo guardamos como String (YYYY-MM-DD) para coincidir con tu frontend

	private String location;

	public ItineraryEvent() {
	}

	public ItineraryEvent(String userEmail, String eventType, String title, String eventDate, String location) {
		this.userEmail = userEmail;
		this.eventType = eventType;
		this.title = title;
		this.eventDate = eventDate;
		this.location = location;
	}

	// Getters y Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}