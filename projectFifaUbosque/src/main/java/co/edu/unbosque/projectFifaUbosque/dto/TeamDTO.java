package co.edu.unbosque.projectFifaUbosque.dto;

import co.edu.unbosque.projectFifaUbosque.model.Team.GroupName;

public class TeamDTO {

	private Long id;
	private String teamName;
	private String shortName;
	private String image;
	private String country;
	private GroupName groupName;
	private int ranking;
	private int points;

	public TeamDTO() {
		// TODO Auto-generated constructor stub
	}

	public TeamDTO(Long id, String teamName, String shortName, String image, String country, GroupName groupName,
			int ranking, int points) {
		super();
		this.id = id;
		this.teamName = teamName;
		this.shortName = shortName;
		this.image = image;
		this.country = country;
		this.groupName = groupName;
		this.ranking = ranking;
		this.points = points;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		return "TeamDTO [id=" + id + ", teamName=" + teamName + ", shortName=" + shortName + ", image=" + image
				+ ", country=" + country + ", groupName=" + groupName + ", ranking=" + ranking + ", points=" + points
				+ "]";
	}

}
