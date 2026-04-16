package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	private String teamName;
	@Column(unique = true)
	private String shortName;
	private String image;
	private String country;

	@Enumerated(EnumType.STRING)
	private GroupName groupName;
	private int ranking;
	private int points;

	public enum GroupName {
		A, B, C, D, E, F, G, H, I, J, K, L
	}

	public Team() {
	}

	public Team(String teamName, String shortName, String image, String country, GroupName groupName, int ranking,
			int points) {
		super();
		this.teamName = teamName;
		this.shortName = shortName;
		this.image = image;
		this.country = country;
		this.groupName = groupName;
		this.ranking = ranking;
		this.points = points;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public GroupName getGroupName() {
		return groupName;
	}

	public void setGroupName(GroupName groupName) {
		this.groupName = groupName;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "Team [teamName=" + teamName + ", shortName=" + shortName + ", image=" + image + ", country=" + country
				+ ", groupName=" + groupName + ", ranking=" + ranking + ", points=" + points + "]";
	}

}
