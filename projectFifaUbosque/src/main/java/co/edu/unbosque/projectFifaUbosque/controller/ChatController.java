package co.edu.unbosque.projectFifaUbosque.controller;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import co.edu.unbosque.projectFifaUbosque.dto.ChatMessageDTO;

@Controller
@CrossOrigin(origins = { "*" })
public class ChatController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private Queue<String> availableAgents = new ConcurrentLinkedQueue<>();

	private ConcurrentHashMap<String, String> activeChats = new ConcurrentHashMap<>();

	@MessageMapping("/chat.agentAvailable")
	public void agentAvailable(@Payload ChatMessageDTO message) {
		String agent = message.getSender();
		if (!availableAgents.contains(agent) && !activeChats.containsValue(agent)) {
			availableAgents.add(agent);
		}
	}

	@MessageMapping("/chat.requestSupport")
	public void requestSupport(@Payload ChatMessageDTO message) {
		String user = message.getSender();
		String agent = availableAgents.poll();

		if (agent != null) {
			activeChats.put(user, agent);
			activeChats.put(agent, user);

			messagingTemplate.convertAndSend("/queue/chat/" + user,
					new ChatMessageDTO(ChatMessageDTO.MessageType.ASSIGN, "SYSTEM", user, agent));

			messagingTemplate.convertAndSend("/queue/chat/" + agent,
					new ChatMessageDTO(ChatMessageDTO.MessageType.ASSIGN, "SYSTEM", agent, user));

		} else {
			messagingTemplate.convertAndSend("/queue/chat/" + user,
					new ChatMessageDTO(ChatMessageDTO.MessageType.WAITING, "SYSTEM", user,
							"No hay agentes disponibles en este momento. Por favor espera."));
		}
	}

	@MessageMapping("/chat.sendMessage")
	public void sendMessage(@Payload ChatMessageDTO message) {
		messagingTemplate.convertAndSend("/queue/chat/" + message.getRecipient(), message);
	}

	@MessageMapping("/chat.closeSession")
	public void closeSession(@Payload ChatMessageDTO message) {
		String sender = message.getSender();
		String peer = activeChats.get(sender);

		if (peer != null) {
			activeChats.remove(sender);
			activeChats.remove(peer);

			messagingTemplate.convertAndSend("/queue/chat/" + peer, new ChatMessageDTO(
					ChatMessageDTO.MessageType.DISCONNECT, "SYSTEM", peer, "El chat ha sido finalizado."));

			if (message.getContent().equals("AGENT")) {
				availableAgents.add(sender);
			} else {
				availableAgents.add(peer);
			}
		}
	}
}