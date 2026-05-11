package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String uuid; 

	@Column(nullable = false)
	private String userEmail; 

	@Column(nullable = false)
	private String matchName;

	private String stadium;

	private String matchDate;

	@Column(name = "purchase_date")
	private LocalDateTime purchaseDate;

	public Ticket() {
	}

	public Ticket(String uuid, String userEmail, String matchName, String stadium, String matchDate,
			LocalDateTime purchaseDate) {
		this.uuid = uuid;
		this.userEmail = userEmail;
		this.matchName = matchName;
		this.stadium = stadium;
		this.matchDate = matchDate;
		this.purchaseDate = purchaseDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public String getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(String matchDate) {
		this.matchDate = matchDate;
	}

	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
}