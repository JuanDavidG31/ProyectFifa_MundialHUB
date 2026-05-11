package co.edu.unbosque.projectFifaUbosque.dto;

public class ChatMessageDTO {
	public enum MessageType {
		CHAT, ASSIGN, DISCONNECT, WAITING, SYSTEM
	}

	private MessageType type;
	private String sender;
	private String recipient;
	private String content;

	public ChatMessageDTO() {
	}

	public ChatMessageDTO(MessageType type, String sender, String recipient, String content) {
		this.type = type;
		this.sender = sender;
		this.recipient = recipient;
		this.content = content;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}