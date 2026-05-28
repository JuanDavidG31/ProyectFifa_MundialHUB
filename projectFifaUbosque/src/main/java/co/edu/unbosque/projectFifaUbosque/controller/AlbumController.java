package co.edu.unbosque.projectFifaUbosque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import co.edu.unbosque.projectFifaUbosque.model.Transaction;
import co.edu.unbosque.projectFifaUbosque.dto.StickerResponseDTO;
import co.edu.unbosque.projectFifaUbosque.model.User;
import co.edu.unbosque.projectFifaUbosque.repository.UserRepository;
import co.edu.unbosque.projectFifaUbosque.service.AlbumService;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;

/**
 * Controlador REST encargado de gestionar todas las interacciones asociadas al
 * Álbum Digital.
 * <p>
 * Provee Endpoints para flujos de transacciones P2P (Peer-to-Peer), apertura de
 * paquetes, compra de ítems, consulta de inventario personal y canje de láminas
 * duplicadas.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/album")
@CrossOrigin(origins = "*")
public class AlbumController {
	/** Capa de servicios lógicos aplicados al álbum digital. */
	@Autowired
	private AlbumService albumService;
	/** Repositorio de consulta directa de entidades de usuario. */
	@Autowired
	private UserRepository userRepo;

	/**
	 * Método auxiliar privado para recuperar un usuario desde la BD cifrando su
	 * username de forma previa.
	 *
	 * @param encryptedUsername Identificador de usuario en texto claro que requiere
	 *                          cifrado.
	 * @return La entidad {@link User} encontrada.
	 * @throws RuntimeException Si el usuario solicitado no existe en la base de
	 *                          datos.
	 */
	private User getUserOrThrow(String encryptedUsername) {
		return userRepo.findByUser(AESUtil.encrypt(encryptedUsername))
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	}

	/**
	 * Envía una invitación para iniciar una negociación o sala de intercambio P2P
	 * entre dos jugadores.
	 *
	 * @param requester Username del cliente solicitante.
	 * @param target    Username del cliente objetivo.
	 * @return {@link ResponseEntity} con el estado o id de la invitación creada, o
	 *         mensaje de error.
	 */
	@PostMapping("/p2p/invite")
	public ResponseEntity<?> inviteP2P(@RequestParam String requester, @RequestParam String target) {
		try {
			return ResponseEntity.ok(albumService.inviteP2P(getUserOrThrow(requester), target));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * Realiza un sondeo (polling) del estado actual de la sala P2P asignada a un
	 * usuario.
	 *
	 * @param username Nombre de usuario a consultar.
	 * @return {@link ResponseEntity} con el estado de la sala actual.
	 */
	@GetMapping("/p2p/poll")
	public ResponseEntity<?> pollP2P(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.pollRoom(username));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Registra una acción específica o selección dentro de la sala de negociación
	 * P2P activa.
	 *
	 * @param username  Nombre del usuario involucrado en la acción.
	 * @param action    Tipo de acción (ej: agregar, confirmar, cancelar).
	 * @param stickerId Identificador opcional de la lámina relacionada a la acción.
	 * @return {@link ResponseEntity} confirmando el resultado de la acción
	 *         efectuada.
	 */
	@PostMapping("/p2p/action")
	public ResponseEntity<?> actionP2P(@RequestParam String username, @RequestParam String action,
			@RequestParam(required = false) Long stickerId) {
		try {
			return ResponseEntity.ok(albumService.actionP2P(getUserOrThrow(username), action, stickerId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * Envía una petición formal de intercambio de láminas tradicional hacia un
	 * usuario destino.
	 *
	 * @param requester Usuario que genera la solicitud.
	 * @param target    Usuario receptor del intercambio.
	 * @return {@link ResponseEntity} indicando la creación de la solicitud.
	 */
	@PostMapping("/p2p/request")
	public ResponseEntity<?> requestP2PExchange(@RequestParam String requester, @RequestParam String target) {
		try {
			return ResponseEntity.ok(albumService.requestPeerToPeerExchange(getUserOrThrow(requester), target));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * Confirma una propuesta de intercambio ingresando el código de seguridad de
	 * doble factor correspondiente.
	 *
	 * @param requester Usuario solicitante original.
	 * @param target    Usuario objetivo.
	 * @param code      Código de verificación suministrado.
	 * @return {@link ResponseEntity} con la respuesta de la confirmación.
	 */
	@PostMapping("/p2p/confirm")
	public ResponseEntity<?> confirmP2PExchange(@RequestParam String requester, @RequestParam String target,
			@RequestParam String code) {
		try {
			return ResponseEntity.ok(albumService.confirmPeerToPeerExchange(getUserOrThrow(requester), target, code));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * Executa la transferencia física/digital definitiva de stickers acordados
	 * mutuamente entre dos usuarios.
	 *
	 * @param requester      Usuario origen de la transacción.
	 * @param target         Usuario destino.
	 * @param myStickerId    Id del sticker que entrega el solicitante.
	 * @param theirStickerId Id del sticker que entrega el receptor.
	 * @return {@link ResponseEntity} con los detalles de la ejecución de la
	 *         transferencia.
	 */
	@PostMapping("/p2p/execute")
	public ResponseEntity<?> executeP2PExchange(@RequestParam String requester, @RequestParam String target,
			@RequestParam Long myStickerId, @RequestParam Long theirStickerId) {
		try {
			return ResponseEntity.ok(
					albumService.executeP2PExchange(getUserOrThrow(requester), target, myStickerId, theirStickerId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * Obtiene la lista completa del historial financiero o de transacciones de un
	 * usuario.
	 *
	 * @param username Nombre del usuario.
	 * @return {@link ResponseEntity} que contiene la lista de {@link Transaction}.
	 */
	@GetMapping("/transactions")
	public ResponseEntity<List<Transaction>> getTransactions(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.getTransactionHistory(getUserOrThrow(username)));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Recupera el catálogo del álbum propio de un usuario, indicando qué láminas
	 * posee y cuáles le faltan.
	 *
	 * @param username Nombre de usuario.
	 * @return Lista empaquetada en DTOs de stickers de la colección del cliente.
	 */
	@GetMapping("/my-album")
	public ResponseEntity<List<StickerResponseDTO>> getMyAlbum(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.getUserAlbum(getUserOrThrow(username)));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Obtiene balances financieros actuales y de inventario base (paquetes
	 * disponibles y monedas acumuladas).
	 *
	 * @param username Identificador único del usuario.
	 * @return Mapa llave-valor indicando las estadísticas del estado del perfil.
	 */
	@GetMapping("/status")
	public ResponseEntity<Map<String, Integer>> getStatus(@RequestParam String username) {
		try {
			User user = getUserOrThrow(username);
			return ResponseEntity.ok(Map.of("availablePacks", user.getAvailablePacks(), "coins", user.getCoins()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Abre un sobre o paquete aleatorio gastando un empaque disponible de los
	 * recursos del usuario.
	 *
	 * @param username Nombre de usuario que realiza la apertura.
	 * @return {@link ResponseEntity} con la lista de láminas obtenidas al azar.
	 */
	@PostMapping("/open")
	public ResponseEntity<?> openPack(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.openPack(getUserOrThrow(username)));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * Intercambia el lote acumulado de stickers repetidos de un usuario por monedas
	 * de la aplicación.
	 *
	 * @param username Nombre del cliente.
	 * @return {@link ResponseEntity} con la cantidad de monedas generadas o estado
	 *         final del proceso.
	 */
	@PostMapping("/exchange")
	public ResponseEntity<?> exchangeDuplicates(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.exchangeDuplicates(getUserOrThrow(username)));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * Endpoint de recompensa especial por haber completado el llenado total del
	 * álbum.
	 *
	 * @param username Nombre del usuario con el álbum al 100%.
	 * @return {@link ResponseEntity} con el resultado del premio adjudicado.
	 */
	@PostMapping("/win1000")
	public ResponseEntity<?> win1000(@RequestParam String username) {
		try {

			User user = getUserOrThrow(username);

			return ResponseEntity.ok(albumService.winAlbum100(user));

		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al procesar el premio"));
		}
	}

	/**
	 * Realiza la transacción de compra de un nuevo sobre virtual descontando
	 * monedas del saldo del usuario.
	 *
	 * @param username Comprador del paquete.
	 * @return {@link ResponseEntity} con mensaje de éxito o denegación por saldo
	 *         insuficiente.
	 */
	@PostMapping("/buy-pack")
	public ResponseEntity<?> buyPack(@RequestParam String username) {
		try {
			albumService.buyPack(getUserOrThrow(username));
			return ResponseEntity.ok(Map.of("message", "Paquete comprado exitosamente"));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}