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

/**
 * Controlador mixto (REST y WebSocket) encargado de gobernar las salas de juego
 * y apuestas sociales ("Pollas").
 * <p>
 * Soportado tanto por llamados síncronos HTTP como por enrutamientos WebSocket
 * con payloads STOMP para coordinar eventos compartidos interactivos en tiempo
 * real.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/betting-rooms")
@CrossOrigin(origins = "*")
public class BettingRoomController {
	/**
	 * Servicio interno para la administración y lógica de juego en salas de pollas.
	 */
	@Autowired
	private BettingRoomService bettingRoomService;

	/**
	 * Consulta y retorna los datos estructurales de la sala de apuestas activa a la
	 * que pertenece un usuario.
	 *
	 * @param username Nombre del usuario.
	 * @return {@link ResponseEntity} con los detalles de la sala, o HTTP 204 No
	 *         Content.
	 */
	@GetMapping("/my-room")
	public ResponseEntity<?> getMyRoom(@RequestParam String username) {
		BettingRoomService.Room room = bettingRoomService.getRoomForUser(username);
		if (room != null) {
			return ResponseEntity.ok(room);
		}
		return ResponseEntity.noContent().build();
	}

	/**
	 * Endpoint de desconexión o expulsión forzada de un usuario de su sala actual.
	 *
	 * @param username Nombre del usuario.
	 * @return Respuesta exitosa HTTP 200.
	 */
	@DeleteMapping("/force-leave")
	public ResponseEntity<?> forceLeave(@RequestParam String username) {
		BettingRoomService.Room room = bettingRoomService.getRoomForUser(username);
		if (room != null) {
			bettingRoomService.leaveRoom(room.getId(), username);
		}
		return ResponseEntity.ok().build();
	}

	/**
	 * Devuelve un catálogo ordenado de todas las salas de apuestas disponibles
	 * creadas en el ecosistema.
	 *
	 * @return Lista completa de salas (Room).
	 */
	@GetMapping("/list")
	public ResponseEntity<List<BettingRoomService.Room>> getRooms() {
		return ResponseEntity.ok(bettingRoomService.getAllAvailableRooms());
	}

	/**
	 * Crea un nuevo espacio exclusivo de apuestas sociales vinculando a un usuario
	 * creador como dueño (owner).
	 *
	 * @param roomName Nombre asignado a la nueva sala.
	 * @param owner    Username del creador.
	 * @return {@link ResponseEntity} con el objeto Room estructurado.
	 */
	@PostMapping("/create")
	public ResponseEntity<?> createRoom(@RequestParam String roomName, @RequestParam String owner) {
		try {
			BettingRoomService.Room newRoom = bettingRoomService.createRoom(roomName, owner);
			return ResponseEntity.ok(newRoom);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * Enrutador WebSocket (STOMP) que notifica y procesa el ingreso de un jugador a
	 * una sala de apuestas. Mapeado en la ruta general: /app/betting.join
	 *
	 * @param message DTO conteniendo datos de remitente y id del canal/sala.
	 */
	@MessageMapping("/betting.join")
	public void joinRoomWebSocket(@Payload BetMessageDTO message) {

		bettingRoomService.joinRoom(message.getRoomId(), message.getSender());
	}

	/**
	 * Enrutador WebSocket (STOMP) que notifica y procesa la salida de un jugador de
	 * la sala actual. Mapeado en la ruta general: /app/betting.leave
	 *
	 * @param message DTO conteniendo la información de salida.
	 */
	@MessageMapping("/betting.leave")
	public void leaveRoomWebSocket(@Payload BetMessageDTO message) {
		bettingRoomService.leaveRoom(message.getRoomId(), message.getSender());
	}

	/**
	 * Transmite y comparte en tiempo real las predicciones o marcadores deportivos
	 * sugeridos por un usuario a la sala. Mapeado en la ruta general:
	 * /app/betting.share
	 *
	 * @param message Datos del marcador e información de la polla compartida.
	 */
	@MessageMapping("/betting.share")
	public void sharePredictionWebSocket(@Payload BetMessageDTO message) {
		bettingRoomService.shareInRoom(message);
	}

	/**
	 * Captura y despacha comandos de acción dinámicos adicionales solicitados por
	 * los clientes mediante WebSockets. Mapeado en la ruta general:
	 * /app/betting.action
	 *
	 * @param message Parámetros de la acción y contexto de ejecución.
	 */
	@MessageMapping("/betting.action")
	public void handleActionWebSocket(@Payload BetMessageDTO message) {
		bettingRoomService.handleRoomAction(message);
	}
}