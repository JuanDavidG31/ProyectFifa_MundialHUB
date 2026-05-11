package co.edu.unbosque.projectFifaUbosque.dto;

import lombok.Data;

@Data
public class BetMessageDTO {
	private String roomId;
	private String sender;
	private String type;
	private String content;

	private Long matchId;
	private String matchInfo;
	private Integer homeScore;
	private Integer awayScore;

	private String targetUser;

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public BetMessageDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	public String getMatchInfo() {
		return matchInfo;
	}

	public void setMatchInfo(String matchInfo) {
		this.matchInfo = matchInfo;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}

}