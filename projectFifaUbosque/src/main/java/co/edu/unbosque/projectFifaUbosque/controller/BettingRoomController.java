package co.edu.unbosque.projectFifaUbosque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import co.edu.unbosque.projectFifaUbosque.dto.BetMessageDTO;
import co.edu.unbosque.projectFifaUbosque.service.BettingRoomService;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;

@RestController
@RequestMapping("/betting-rooms")
@CrossOrigin(origins = "*")
public class BettingRoomController {

	@Autowired
	private BettingRoomService bettingRoomService;

	@GetMapping("/my-room")
	public ResponseEntity<?> getMyRoom(@RequestParam String username) {
		BettingRoomService.Room room = bettingRoomService.getRoomForUser(username);
		if (room != null) {
			return ResponseEntity.ok(room);
		}
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/force-leave")
	public ResponseEntity<?> forceLeave(@RequestParam String username) {
		BettingRoomService.Room room = bettingRoomService.getRoomForUser(username);
		if (room != null) {
			bettingRoomService.leaveRoom(room.getId(), username);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/list")
	public ResponseEntity<List<BettingRoomService.Room>> getRooms() {
		return ResponseEntity.ok(bettingRoomService.getAllAvailableRooms());
	}

	@PostMapping("/create")
	public ResponseEntity<?> createRoom(@RequestParam String roomName, @RequestParam String owner) {
		try {
			BettingRoomService.Room newRoom = bettingRoomService.createRoom(roomName, owner);
			return ResponseEntity.ok(newRoom);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@MessageMapping("/betting.join")
	public void joinRoomWebSocket(@Payload BetMessageDTO message) {

		bettingRoomService.joinRoom(message.getRoomId(), message.getSender());
	}

	@MessageMapping("/betting.leave")
	public void leaveRoomWebSocket(@Payload BetMessageDTO message) {
		bettingRoomService.leaveRoom(message.getRoomId(), message.getSender());
	}

	@MessageMapping("/betting.share")
	public void sharePredictionWebSocket(@Payload BetMessageDTO message) {
		bettingRoomService.shareInRoom(message);
	}

	@MessageMapping("/betting.action")
	public void handleActionWebSocket(@Payload BetMessageDTO message) {
		bettingRoomService.handleRoomAction(message);
	}
}