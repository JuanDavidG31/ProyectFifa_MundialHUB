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

	private Map<String, Room> activeRooms = new ConcurrentHashMap<>();

	public Room createRoom(String roomName, String owner) {
		boolean nameExists = activeRooms.values().stream().anyMatch(r -> r.getName().equalsIgnoreCase(roomName));

		if (nameExists) {
			throw new IllegalArgumentException("Ya existe una sala con el nombre: " + roomName);
		}

		Room newRoom = new Room();
		newRoom.setId(UUID.randomUUID().toString().substring(0, 8)); 
		newRoom.setName(roomName);
		newRoom.setOwner(owner);
		newRoom.getMembers().add(owner);

		activeRooms.put(newRoom.getId(), newRoom);
		return newRoom;
	}

	public Room joinRoom(String roomId, String username) {
		Room room = activeRooms.get(roomId);
		if (room != null) {
			room.getMembers().add(username);

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
			
			
		}
	}

	public void leaveRoom(String roomId, String username) {
		Room room = activeRooms.get(roomId);
		if (room != null) {

			if (room.getOwner().equals(username)) {

				BetMessageDTO notice = new BetMessageDTO();
				notice.setRoomId(roomId);
				notice.setSender("SYSTEM");
				notice.setType("ROOM_CLOSED"); 
				notice.setContent("El creador ha cerrado la sala.");

				for (String member : room.getMembers()) {
					messagingTemplate.convertAndSend("/queue/betting/" + member, notice);
				}

				activeRooms.remove(roomId);

			} else {
				room.getMembers().remove(username);

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

	public void shareInRoom(BetMessageDTO message) {
		Room room = activeRooms.get(message.getRoomId());
		if (room != null) {
			room.getSharedHistory().add(message);

			if (room.getSharedHistory().size() > 50) {
				room.getSharedHistory().remove(0);
			}

			broadcastRoomUpdate(room);
		}
	}

	private void broadcastRoomUpdate(Room room) {
		for (String member : room.getMembers()) {
			
			messagingTemplate.convertAndSend("/queue/betting/" + member, room);
		}
	}

	public List<Room> getAllAvailableRooms() {
		return List.copyOf(activeRooms.values());
	}

	public void handleRoomAction(BetMessageDTO message) {
		Room room = activeRooms.get(message.getRoomId());
		if (room == null)
			return;

		switch (message.getType()) {
		case "JOIN_REQUEST":
			messagingTemplate.convertAndSend("/queue/betting/" + room.getOwner(), message);
			break;

		case "JOIN_ACCEPT":
			if (message.getSender().equals(room.getOwner())) {
				room.getMembers().add(message.getTargetUser());

				BetMessageDTO notice = new BetMessageDTO();
				notice.setRoomId(room.getId());
				notice.setSender("SYSTEM");
				notice.setType("JOIN");
				notice.setContent("¡" + message.getTargetUser() + " ha sido aceptado en la sala!");
				room.getSharedHistory().add(notice);

				broadcastRoomUpdate(room);
			}
			break;

		case "JOIN_DENY":
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