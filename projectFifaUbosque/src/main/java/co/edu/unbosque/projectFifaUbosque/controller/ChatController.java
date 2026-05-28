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

/**
 * Controlador WebSocket encargado de la mensajería instantánea del chat
 * bidireccional de Servicio al Cliente.
 * <p>
 * Administra de forma concurrente colas de agentes de soporte técnico
 * disponibles y mapas hilos activos conectando directamente a clientes en
 * estado de espera con agentes libres en tiempo real.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Controller
@CrossOrigin(origins = { "*" })
public class ChatController {
	/**
	 * Plantilla de mensajería asíncrona de Spring para inyectar payloads directo a
	 * colas personalizadas.
	 */
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	/**
	 * Cola con soporte seguro de concurrencia conteniendo los identificadores de
	 * agentes de soporte libres.
	 */
	private Queue<String> availableAgents = new ConcurrentLinkedQueue<>();
	/**
	 * Estructura de mapeo bidireccional concurrente para registrar sesiones
	 * cruzadas estables (Usuario - Agente).
	 */
	private ConcurrentHashMap<String, String> activeChats = new ConcurrentHashMap<>();

	/**
	 * Mapea la cola interna cuando un usuario del tipo 'SUPPORT' se conecta y se
	 * declara disponible para atender. Mapeado en: /app/chat.agentAvailable
	 *
	 * @param message DTO con la metadata y nombre del agente solicitante.
	 */
	@MessageMapping("/chat.agentAvailable")
	public void agentAvailable(@Payload ChatMessageDTO message) {
		String agent = message.getSender();
		if (!availableAgents.contains(agent) && !activeChats.containsValue(agent)) {
			availableAgents.add(agent);
		}
	}

	/**
	 * Evalúa las solicitudes entrantes de soporte por parte de clientes corrientes.
	 * Si existen agentes libres los empareja de inmediato; en caso contrario, emite
	 * un mensaje con estado 'WAITING'. Mapeado en: /app/chat.requestSupport
	 *
	 * @param message Objeto con la información del usuario solicitante de ayuda.
	 */
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

	/**
	 * Transmite y despacha el cuerpo del mensaje de texto instantáneo directamente
	 * a la cola de destino privada del receptor. Mapeado en: /app/chat.sendMessage
	 *
	 * @param message Objeto de mensajería con destinatario y contenido en texto
	 *                claro.
	 */
	@MessageMapping("/chat.sendMessage")
	public void sendMessage(@Payload ChatMessageDTO message) {
		messagingTemplate.convertAndSend("/queue/chat/" + message.getRecipient(), message);
	}

	/**
	 * Cierra y disuelve formalmente la vinculación de comunicación existente en el
	 * mapa interactivo de soporte. Devuelve automáticamente al agente involucrado a
	 * la cola de disponibilidad del sistema. Mapeado en: /app/chat.closeSession
	 *
	 * @param message Información de cierre que contiene qué rol o persona solicitó
	 *                la desconexión.
	 */
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