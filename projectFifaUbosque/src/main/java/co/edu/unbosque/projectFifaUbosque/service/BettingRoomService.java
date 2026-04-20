package co.edu.unbosque.projectFifaUbosque.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import co.edu.unbosque.projectFifaUbosque.dto.BetMessageDTO;

@Service
public class BettingRoomService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// Estructura de la Sala de Apuestas
	@lombok.Data
	public static class Room {
		private String id;
		private String name;
		private String owner;
		private Set<String> members = ConcurrentHashMap.newKeySet();
		private List<BetMessageDTO> sharedHistory = new CopyOnWriteArrayList<>();

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public Set<String> getMembers() {
			return members;
		}

		public void setMembers(Set<String> members) {
			this.members = members;
		}

		public List<BetMessageDTO> getSharedHistory() {
			return sharedHistory;
		}

		public void setSharedHistory(List<BetMessageDTO> sharedHistory) {
			this.sharedHistory = sharedHistory;
		}

	}

	// Memoria principal de salas activas
	private Map<String, Room> activeRooms = new ConcurrentHashMap<>();

	// 1. Crear una sala nueva
	public Room createRoom(String roomName, String owner) {
		// 🌟 NUEVO: Validar que no exista ya una sala con ese mismo nombre
		boolean nameExists = activeRooms.values().stream().anyMatch(r -> r.getName().equalsIgnoreCase(roomName));

		if (nameExists) {
			throw new IllegalArgumentException("Ya existe una sala con el nombre: " + roomName);
		}

		Room newRoom = new Room();
		newRoom.setId(UUID.randomUUID().toString().substring(0, 8)); // ID corto
		newRoom.setName(roomName);
		newRoom.setOwner(owner);
		newRoom.getMembers().add(owner);

		activeRooms.put(newRoom.getId(), newRoom);
		return newRoom;
	}

	// 2. Unirse a una sala
	public Room joinRoom(String roomId, String username) {
		Room room = activeRooms.get(roomId);
		if (room != null) {
			room.getMembers().add(username);

			// Notificamos a la sala que alguien entró
			BetMessageDTO notice = new BetMessageDTO();
			notice.setRoomId(roomId);
			notice.setSender("SYSTEM");
			notice.setType("JOIN");
			notice.setContent("El usuario " + username + " se ha unido a la sala.");
			shareInRoom(notice);
		}
		return room;
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		if (headerAccessor.getUser() != null) {
			String username = headerAccessor.getUser().getName();
			// 🌟 SOLUCIÓN: Ya no llamamos a leaveRoom().
			// Simplemente dejamos registro de que se desconectó el WebSocket, pero su
			// puesto en la sala sigue intacto.
			System.out.println(
					"🔌 [WEBSOCKET] Desconexión detectada de " + username + ". Manteniendo su sesión en la sala.");
		}
	}

	public void leaveRoom(String roomId, String username) {
		Room room = activeRooms.get(roomId);
		if (room != null) {

			// 🌟 NUEVO: Si el que se sale es el creador/dueño, destruimos la sala
			if (room.getOwner().equals(username)) {

				// 1. Preparamos el mensaje de expulsión
				BetMessageDTO notice = new BetMessageDTO();
				notice.setRoomId(roomId);
				notice.setSender("SYSTEM");
				notice.setType("ROOM_CLOSED"); // Tipo especial para avisar al frontend
				notice.setContent("El creador ha cerrado la sala.");

				// 2. Le avisamos a todos los miembros de la sala antes de borrarla
				for (String member : room.getMembers()) {
					messagingTemplate.convertAndSend("/queue/betting/" + member, notice);
				}

				// 3. Eliminamos la sala completamente
				activeRooms.remove(roomId);

			} else {
				// Lógica normal si el que sale es un jugador invitado
				room.getMembers().remove(username);

				// Si la sala queda vacía, se destruye
				if (room.getMembers().isEmpty()) {
					activeRooms.remove(roomId);
				} else {
					BetMessageDTO notice = new BetMessageDTO();
					notice.setRoomId(roomId);
					notice.setSender("SYSTEM");
					notice.setType("LEAVE");
					notice.setContent(username + " ha abandonado la sala.");
					shareInRoom(notice);
				}
			}
		}
	}

	// 4. Compartir predicción o mensaje en la sala
	public void shareInRoom(BetMessageDTO message) {
		Room room = activeRooms.get(message.getRoomId());
		if (room != null) {
			// Guardamos el mensaje en el historial de la sala
			room.getSharedHistory().add(message);

			// Limitamos el historial a 50 mensajes para no saturar memoria
			if (room.getSharedHistory().size() > 50) {
				room.getSharedHistory().remove(0);
			}

			broadcastRoomUpdate(room);
		}
	}

	// 5. Enviar el estado actualizado a todos los miembros
	private void broadcastRoomUpdate(Room room) {
		for (String member : room.getMembers()) {
			System.out.println("🟢 [WEBSOCKET BETTING] Actualizando sala para: " + member);
			// Enviamos directamente a la cola específica de cada usuario (Igual que en
			// ChatController)
			messagingTemplate.convertAndSend("/queue/betting/" + member, room);
		}
	}

	// Obtener todas las salas activas (para mostrarlas en un lobby)
	public List<Room> getAllAvailableRooms() {
		return List.copyOf(activeRooms.values());
	}

	// Procesar las solicitudes de unirse, aceptar o denegar
	public void handleRoomAction(BetMessageDTO message) {
		Room room = activeRooms.get(message.getRoomId());
		if (room == null)
			return;

		switch (message.getType()) {
		case "JOIN_REQUEST":
			// El usuario pide unirse. Le enviamos la notificación SOLO al dueño de la sala
			messagingTemplate.convertAndSend("/queue/betting/" + room.getOwner(), message);
			break;

		case "JOIN_ACCEPT":
			// El dueño aceptó. Añadimos al usuario a la lista oficial de miembros
			if (message.getSender().equals(room.getOwner())) {
				room.getMembers().add(message.getTargetUser());

				// Opcional: Anunciamos en el chat que alguien nuevo entró
				BetMessageDTO notice = new BetMessageDTO();
				notice.setRoomId(room.getId());
				notice.setSender("SYSTEM");
				notice.setType("JOIN");
				notice.setContent("¡" + message.getTargetUser() + " ha sido aceptado en la sala!");
				room.getSharedHistory().add(notice);

				// Al enviar este broadcast, al nuevo usuario se le abrirá la vista de la sala
				broadcastRoomUpdate(room);
			}
			break;

		case "JOIN_DENY":
			// El dueño rechazó. Le mandamos un mensaje de rechazo solo a ese usuario
			if (message.getSender().equals(room.getOwner())) {
				messagingTemplate.convertAndSend("/queue/betting/" + message.getTargetUser(), message);
			}
			break;
		}
	}

	public Room getRoomForUser(String username) {
		return activeRooms.values().stream().filter(r -> r.getMembers().contains(username)).findFirst().orElse(null);
	}
}