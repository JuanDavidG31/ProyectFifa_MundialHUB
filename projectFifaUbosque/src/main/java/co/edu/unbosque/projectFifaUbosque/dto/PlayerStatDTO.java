package co.edu.unbosque.projectFifaUbosque.dto;

public class PlayerStatDTO {
	private String name;
	private String team;
	private String flagUrl;
	private int value;

	public PlayerStatDTO(String name, String team, String flagUrl, int value) {
		this.name = name;
		this.team = team;
		this.flagUrl = flagUrl;
		this.value = value;
	}

	// Getters y Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getFlagUrl() {
		return flagUrl;
	}

	public void setFlagUrl(String flagUrl) {
		this.flagUrl = flagUrl;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}