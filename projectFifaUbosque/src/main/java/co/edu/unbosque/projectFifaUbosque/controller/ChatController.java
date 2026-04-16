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

	// Cola de agentes de soporte disponibles (guarda sus usernames)
	private Queue<String> availableAgents = new ConcurrentLinkedQueue<>();
	// Mapa de chats activos (Key: User, Value: Agent) y viceversa para búsqueda
	// rápida
	private ConcurrentHashMap<String, String> activeChats = new ConcurrentHashMap<>();

	// 1. Un agente entra a la vista de Soporte y se reporta disponible
	@MessageMapping("/chat.agentAvailable")
	public void agentAvailable(@Payload ChatMessageDTO message) {
		String agent = message.getSender();
		if (!availableAgents.contains(agent) && !activeChats.containsValue(agent)) {
			availableAgents.add(agent);
			System.out.println("🎧 Agente de soporte disponible: " + agent);
		}
	}

	// 2. Un usuario solicita soporte desde el Home
		@MessageMapping("/chat.requestSupport")
		public void requestSupport(@Payload ChatMessageDTO message) {
			String user = message.getSender();
			String agent = availableAgents.poll(); // Saca al primer agente disponible

			if (agent != null) {
				activeChats.put(user, agent);
				activeChats.put(agent, user);

				// CAMBIO: Envío directo a la cola específica del usuario
				messagingTemplate.convertAndSend("/queue/chat/" + user,
						new ChatMessageDTO(ChatMessageDTO.MessageType.ASSIGN, "SYSTEM", user, agent));

				// CAMBIO: Envío directo a la cola específica del agente
				messagingTemplate.convertAndSend("/queue/chat/" + agent,
						new ChatMessageDTO(ChatMessageDTO.MessageType.ASSIGN, "SYSTEM", agent, user));

				System.out.println("🤝 Chat iniciado: " + user + " <-> " + agent);
			} else {
				// CAMBIO: Envío directo a la cola específica del usuario
				messagingTemplate.convertAndSend("/queue/chat/" + user,
						new ChatMessageDTO(ChatMessageDTO.MessageType.WAITING, "SYSTEM", user,
								"No hay agentes disponibles en este momento. Por favor espera."));
			}
		}

		// 3. Envío de mensajes normales de chat
		@MessageMapping("/chat.sendMessage")
		public void sendMessage(@Payload ChatMessageDTO message) {
			// CAMBIO: Envío directo
			messagingTemplate.convertAndSend("/queue/chat/" + message.getRecipient(), message);
		}

		// 4. Cualquiera de los dos cierra el chat
		@MessageMapping("/chat.closeSession")
		public void closeSession(@Payload ChatMessageDTO message) {
			String sender = message.getSender();
			String peer = activeChats.get(sender);

			if (peer != null) {
				activeChats.remove(sender);
				activeChats.remove(peer);

				// CAMBIO: Envío directo
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