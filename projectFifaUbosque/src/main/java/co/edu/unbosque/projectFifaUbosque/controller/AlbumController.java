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

@RestController
@RequestMapping("/album")
@CrossOrigin(origins = "*")
public class AlbumController {

	@Autowired
	private AlbumService albumService;
	@Autowired
	private UserRepository userRepo;

	private User getUserOrThrow(String encryptedUsername) {
		return userRepo.findByUser(AESUtil.encrypt(encryptedUsername))
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	}

	@PostMapping("/p2p/invite")
	public ResponseEntity<?> inviteP2P(@RequestParam String requester, @RequestParam String target) {
		try {
			return ResponseEntity.ok(albumService.inviteP2P(getUserOrThrow(requester), target));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/p2p/poll")
	public ResponseEntity<?> pollP2P(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.pollRoom(username));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/p2p/action")
	public ResponseEntity<?> actionP2P(@RequestParam String username, @RequestParam String action,
			@RequestParam(required = false) Long stickerId) {
		try {
			return ResponseEntity.ok(albumService.actionP2P(getUserOrThrow(username), action, stickerId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/p2p/request")
	public ResponseEntity<?> requestP2PExchange(@RequestParam String requester, @RequestParam String target) {
		try {
			return ResponseEntity.ok(albumService.requestPeerToPeerExchange(getUserOrThrow(requester), target));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/p2p/confirm")
	public ResponseEntity<?> confirmP2PExchange(@RequestParam String requester, @RequestParam String target,
			@RequestParam String code) {
		try {
			return ResponseEntity.ok(albumService.confirmPeerToPeerExchange(getUserOrThrow(requester), target, code));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

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

	@GetMapping("/transactions")
	public ResponseEntity<List<Transaction>> getTransactions(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.getTransactionHistory(getUserOrThrow(username)));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/my-album")
	public ResponseEntity<List<StickerResponseDTO>> getMyAlbum(@RequestParam String username) {
		try {
			return ResponseEntity.ok(albumService.getUserAlbum(getUserOrThrow(username)));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/status")
	public ResponseEntity<Map<String, Integer>> getStatus(@RequestParam String username) {
		try {
			User user = getUserOrThrow(username);
			return ResponseEntity.ok(Map.of("availablePacks", user.getAvailablePacks(), "coins", user.getCoins()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

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