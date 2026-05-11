package co.edu.unbosque.projectFifaUbosque.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import co.edu.unbosque.projectFifaUbosque.dto.StickerResponseDTO;
import co.edu.unbosque.projectFifaUbosque.model.*;
import co.edu.unbosque.projectFifaUbosque.repository.*;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;

@Service
public class AlbumService {

	@Autowired
	private StickerRepository stickerRepo;
	@Autowired
	private UserStickerRepository userStickerRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private TransactionRepository transactionRepo;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	private static final int PACK_COST_IN_COINS = 10;
	@Autowired
	private EmailService emailService;

	private Map<String, String> activeExchangeCodes = new ConcurrentHashMap<>();

	
	@lombok.Data 
	public static class ExchangeRoom {
		public String requester;
		public String target;
		public String status;
		public Long requesterStickerId;
		public Long targetStickerId;
		public boolean requesterReady;
		public boolean targetReady;
		public List<Map<String, Object>> requesterInventory;
		public List<Map<String, Object>> targetInventory;
	}

	private Map<String, ExchangeRoom> activeRooms = new ConcurrentHashMap<>();

	public ExchangeRoom pollRoom(String username) {
		return activeRooms.values().stream().filter(r -> r.requester.equals(username) || r.target.equals(username))
				.findFirst().orElse(null);
	}

	private void broadcastRoomUpdate(ExchangeRoom room) {
		if (room != null) {
			try {
				
				messagingTemplate.convertAndSendToUser(room.requester, "/queue/exchange", room);
				messagingTemplate.convertAndSendToUser(room.target, "/queue/exchange", room);
			} catch (Exception e) {
			}
		}
	}

	public Map<String, String> inviteP2P(User requester, String targetUsername) {
		User target = userRepo.findByUser(AESUtil.encrypt(targetUsername))
				.orElseThrow(() -> new IllegalStateException("El usuario destino no existe."));
		if (requester.getId().equals(target.getId())) {
			throw new IllegalStateException("No puedes intercambiar contigo mismo.");
		}

		String reqName = AESUtil.decrypt(requester.getUsername());
		activeRooms.values().removeIf(r -> r.requester.equals(reqName) || r.target.equals(reqName));

		ExchangeRoom room = new ExchangeRoom();
		room.requester = reqName;
		room.target = targetUsername;
		room.status = "PENDING";
		activeRooms.put(reqName + "_" + targetUsername, room);
		broadcastRoomUpdate(room);
		return Map.of("message", "Invitación enviada. Esperando respuesta...");
	}

	@Transactional
	public Map<String, String> actionP2P(User user, String action, Long stickerId) {
		String un = AESUtil.decrypt(user.getUsername());
		ExchangeRoom room = pollRoom(un);
		if (room == null)
			return Map.of("message", "No hay sala activa.");

		boolean isRequester = room.requester.equals(un);

		switch (action) {
		case "ACCEPT":
			room.status = "ACCEPTED";
			User targetUser = userRepo.findByUser(user.getUsername()).get();
			User reqUser = userRepo.findByUser(AESUtil.encrypt(room.requester)).get();
			room.targetInventory = getDuplicatesList(targetUser);
			room.requesterInventory = getDuplicatesList(reqUser);
			break;
		case "REJECT":
		case "LEAVE":
			activeRooms.values().removeIf(r -> r.requester.equals(un) || r.target.equals(un));
			break;
		case "SELECT":
			if (isRequester)
				room.requesterStickerId = stickerId;
			else
				room.targetStickerId = stickerId;
			break;
		case "READY":
			if (isRequester)
				room.requesterReady = true;
			else
				room.targetReady = true;

			
			if (room.requesterReady && room.targetReady) {
				executeSwapInternal(room);
				room.status = "EXECUTED";
			}
			break;
		}
		broadcastRoomUpdate(room);
		return Map.of("message", "Acción " + action + " procesada.");
	}

	private void executeSwapInternal(ExchangeRoom room) {
		User requester = userRepo.findByUser(AESUtil.encrypt(room.requester)).get();
		User target = userRepo.findByUser(AESUtil.encrypt(room.target)).get();

		UserSticker myUs = userStickerRepo.findByUser(requester).stream()
				.filter(us -> us.getSticker().getId().equals(room.requesterStickerId)).findFirst().get();
		UserSticker theirUs = userStickerRepo.findByUser(target).stream()
				.filter(us -> us.getSticker().getId().equals(room.targetStickerId)).findFirst().get();

		myUs.setQuantity(myUs.getQuantity() - 1);
		theirUs.setQuantity(theirUs.getQuantity() - 1);

		UserSticker myNewUs = userStickerRepo.findByUser(requester).stream()
				.filter(us -> us.getSticker().getId().equals(room.targetStickerId)).findFirst()
				.orElse(new UserSticker(requester, theirUs.getSticker(), 0));
		myNewUs.setQuantity(myNewUs.getQuantity() + 1);

		UserSticker theirNewUs = userStickerRepo.findByUser(target).stream()
				.filter(us -> us.getSticker().getId().equals(room.requesterStickerId)).findFirst()
				.orElse(new UserSticker(target, myUs.getSticker(), 0));
		theirNewUs.setQuantity(theirNewUs.getQuantity() + 1);

		userStickerRepo.saveAll(List.of(myUs, theirUs, myNewUs, theirNewUs));

		transactionRepo.save(new Transaction(requester, "P2P_EXCHANGE", 0, "Intercambio con " + room.target
				+ ". Diste: " + myUs.getSticker().getCode() + " | Recibiste: " + theirUs.getSticker().getCode()));
		transactionRepo.save(new Transaction(target, "P2P_EXCHANGE", 0, "Intercambio con " + room.requester
				+ ". Diste: " + theirUs.getSticker().getCode() + " | Recibiste: " + myUs.getSticker().getCode()));
	}

	public Map<String, String> requestPeerToPeerExchange(User requester, String targetUsername) {

		User target = userRepo.findByUser(AESUtil.encrypt(targetUsername))
				.orElseThrow(() -> new IllegalStateException("El usuario destino no existe."));

		if (requester.getId().equals(target.getId())) {
			throw new IllegalStateException("No puedes intercambiar contigo mismo.");
		}

		String code = String.format("%06d", new Random().nextInt(999999));

		String exchangeKey = requester.getUsername() + "_" + targetUsername;
		activeExchangeCodes.put(exchangeKey, code);
		String requesterName = AESUtil.decrypt(requester.getUsername());
		String targetName = AESUtil.decrypt(target.getName());
		String targetEmail = AESUtil.decrypt(target.getEmail());
		String subject = "Solicitud de Intercambio - Mundial Hub 2026";
		String body = "Hola " + targetName + ",\n\n" + "El usuario " + requesterName
				+ " quiere realizar un intercambio de láminas contigo.\n"
				+ "Si estás de acuerdo, compártele el siguiente código de seguridad para iniciar el proceso:\n\n"
				+ "CÓDIGO: " + code + "\n\n" + "Si no esperabas esto, ignora este mensaje.";

		emailService.sendEmail(targetEmail, subject, body);

		return Map.of("message", "Código enviado exitosamente al correo de " + targetUsername);
	}

	@Transactional
	public Map<String, Object> confirmPeerToPeerExchange(User requester, String targetUsername, String code) {
		String exchangeKey = requester.getUsername() + "_" + targetUsername;
		String validCode = activeExchangeCodes.get(exchangeKey);

		if (validCode == null || !validCode.equals(code)) {
			throw new IllegalStateException("El código es incorrecto o ha expirado.");
		}

		User target = userRepo.findByUser(AESUtil.encrypt(targetUsername))
				.orElseThrow(() -> new IllegalStateException("El usuario destino no existe."));

		List<Map<String, Object>> myDuplicates = getDuplicatesList(requester);
		List<Map<String, Object>> theirDuplicates = getDuplicatesList(target);

		activeExchangeCodes.remove(exchangeKey);

		return Map.of("message", "¡Conexión establecida con " + targetUsername + "!", "myDuplicates", myDuplicates,
				"theirDuplicates", theirDuplicates);
	}

	private List<Map<String, Object>> getDuplicatesList(User user) {
		return userStickerRepo.findByUser(user).stream().filter(us -> us.getQuantity() > 1).map(us -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", us.getSticker().getId());
			map.put("code", us.getSticker().getCode());
			map.put("title", us.getSticker().getTitle());
			map.put("rarity", us.getSticker().getRarity());
			return map;
		}).collect(Collectors.toList());
	}

	@Transactional
	public Map<String, String> executeP2PExchange(User requester, String targetUsername, Long myStickerId,
			Long theirStickerId) {
		User target = userRepo.findByUser(AESUtil.encrypt(targetUsername))
				.orElseThrow(() -> new IllegalStateException("El usuario destino no existe."));

		UserSticker myUs = userStickerRepo.findByUser(requester).stream()
				.filter(us -> us.getSticker().getId().equals(myStickerId)).findFirst()
				.orElseThrow(() -> new IllegalStateException("No tienes esa lámina para dar."));

		UserSticker theirUs = userStickerRepo.findByUser(target).stream()
				.filter(us -> us.getSticker().getId().equals(theirStickerId)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Tu amigo ya no tiene esa lámina repetida."));

		if (myUs.getQuantity() <= 1 || theirUs.getQuantity() <= 1) {
			throw new IllegalStateException("Una de las láminas ya no está repetida y no se puede cambiar.");
		}

		myUs.setQuantity(myUs.getQuantity() - 1);
		theirUs.setQuantity(theirUs.getQuantity() - 1);

		UserSticker myNewUs = userStickerRepo.findByUser(requester).stream()
				.filter(us -> us.getSticker().getId().equals(theirStickerId)).findFirst()
				.orElse(new UserSticker(requester, theirUs.getSticker(), 0));
		myNewUs.setQuantity(myNewUs.getQuantity() + 1);

		UserSticker theirNewUs = userStickerRepo.findByUser(target).stream()
				.filter(us -> us.getSticker().getId().equals(myStickerId)).findFirst()
				.orElse(new UserSticker(target, myUs.getSticker(), 0));
		theirNewUs.setQuantity(theirNewUs.getQuantity() + 1);

		userStickerRepo.saveAll(List.of(myUs, theirUs, myNewUs, theirNewUs));

		String requesterName = AESUtil.decrypt(requester.getName());
		String targetName = AESUtil.decrypt(target.getName());

		transactionRepo.save(new Transaction(requester, "P2P_EXCHANGE", 0,

				"Intercambio con " + targetName + " 🤝. Diste: " + myUs.getSticker().getCode() + " | Recibiste: "
						+ theirUs.getSticker().getCode()));

		transactionRepo.save(new Transaction(target, "P2P_EXCHANGE", 0, "Intercambio con " + requesterName
				+ " 🤝. Diste: " + theirUs.getSticker().getCode() + " | Recibiste: " + myUs.getSticker().getCode()));

		return Map.of("message", "¡Intercambio de cartas realizado con éxito!");
	}

	public List<StickerResponseDTO> getUserAlbum(User user) {
		List<Sticker> catalog = stickerRepo.findAll();
		List<UserSticker> myStickers = userStickerRepo.findByUser(user);

		Map<Long, UserSticker> myStickersMap = myStickers.stream()
				.collect(Collectors.toMap(us -> us.getSticker().getId(), us -> us));

		return catalog.stream().map(sticker -> {
			UserSticker owned = myStickersMap.get(sticker.getId());
			boolean isOwned = owned != null;
			int duplicates = isOwned ? Math.max(0, owned.getQuantity() - 1) : 0;

			return new StickerResponseDTO(sticker.getCode(), sticker.getTitle(), sticker.getSectionId(),
					sticker.getPageTitle(), sticker.getImageUrl(), isOwned, duplicates, sticker.getRarity());
		}).collect(Collectors.toList());
	}

	@Transactional
	public List<StickerResponseDTO> openPack(User user) {
		if (user.getAvailablePacks() <= 0) {
			throw new IllegalStateException("No tienes paquetes disponibles.");
		}

		user.setAvailablePacks(user.getAvailablePacks() - 1);
		userRepo.save(user);

		List<Sticker> allStickers = stickerRepo.findAll();
		List<Sticker> comunes = allStickers.stream().filter(s -> "Común".equalsIgnoreCase(s.getRarity()))
				.collect(Collectors.toList());
		List<Sticker> epicas = allStickers.stream().filter(s -> "Épica".equalsIgnoreCase(s.getRarity()))
				.collect(Collectors.toList());
		List<Sticker> legendarias = allStickers.stream().filter(s -> "Legendaria".equalsIgnoreCase(s.getRarity()))
				.collect(Collectors.toList());

		if (comunes.isEmpty())
			comunes = allStickers;
		if (epicas.isEmpty())
			epicas = allStickers;
		if (legendarias.isEmpty())
			legendarias = allStickers;

		List<Sticker> drawnStickers = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 5; i++) {
			int chance = random.nextInt(100) + 1;
			Sticker selectedSticker;

			if (chance <= 75) {
				selectedSticker = comunes.get(random.nextInt(comunes.size()));
			} else if (chance <= 95) {

				selectedSticker = epicas.get(random.nextInt(epicas.size()));
			} else {
				selectedSticker = legendarias.get(random.nextInt(legendarias.size()));
			}

			drawnStickers.add(selectedSticker);
		}

		List<StickerResponseDTO> packResults = new ArrayList<>();

		for (Sticker s : drawnStickers) {
			Optional<UserSticker> existing = userStickerRepo.findByUserAndSticker_Code(user, s.getCode());
			if (existing.isPresent()) {
				UserSticker us = existing.get();
				us.setQuantity(us.getQuantity() + 1);
				userStickerRepo.save(us);
				packResults.add(createDTO(s, us.getQuantity() - 1));
			} else {
				userStickerRepo.save(new UserSticker(user, s, 1));
				packResults.add(createDTO(s, 0));
			}
		}

		transactionRepo
				.save(new Transaction(user, "PACK_OPENED", 1, "Paquete abierto. Quedan: " + user.getAvailablePacks()));
		return packResults;
	}

	@Transactional
	public Map<String, Object> winAlbum100(User user) {
		if (user.isAlbumCompleteReward()) {
			throw new IllegalStateException("Ya has reclamado el premio por completar el álbum.");
		}

		user.setAlbumCompleteReward(true);

		user.setCoins(user.getCoins() + 1000);

		userRepo.save(user);

		transactionRepo.save(
				new Transaction(user, "ALBUM_COMPLETED_REWARD", 1000, "Premio por completar el 100% de la colección"));

		return Map.of("message", "¡Felicidades! Premio de 1000 monedas reclamado.", "newCoinsBalance", user.getCoins());
	}

	@Transactional
	public Map<String, Integer> exchangeDuplicates(User user) {
		int MAX_EXCHANGES_PER_DAY = 10;
		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

		long exchangesToday = transactionRepo.countTransactionsByUserAndTypeSince(user, "DUPLICATES_EXCHANGED",
				startOfDay);

		if (exchangesToday >= MAX_EXCHANGES_PER_DAY) {
			throw new IllegalStateException("Límite diario alcanzado. Solo puedes intercambiar repetidas "
					+ MAX_EXCHANGES_PER_DAY + " veces al día para evitar abusos.");
		}

		List<UserSticker> inventory = userStickerRepo.findByUser(user);
		int coinsGained = 0;
		int duplicatesBurned = 0;

		for (UserSticker us : inventory) {
			if (us.getQuantity() > 1) {
				int duplicates = us.getQuantity() - 1;

				coinsGained += (duplicates * us.getSticker().getExchangeValue());
				duplicatesBurned += duplicates;

				us.setQuantity(1);
				userStickerRepo.save(us);
			}
		}

		if (duplicatesBurned == 0) {
			throw new IllegalStateException("No tienes láminas repetidas para intercambiar.");
		}

		if (coinsGained > 0) {
			user.setCoins(user.getCoins() + coinsGained);
			userRepo.save(user);
			transactionRepo.save(new Transaction(user, "DUPLICATES_EXCHANGED", coinsGained,
					"Quemadas " + duplicatesBurned + " repetidas."));
		}

		return Map.of("coinsGained", coinsGained, "newTotalCoins", user.getCoins());
	}

	@Transactional
	public void buyPack(User user) {
		if (user.getCoins() < PACK_COST_IN_COINS) {
			throw new IllegalStateException("Monedas insuficientes. Necesitas " + PACK_COST_IN_COINS);
		}
		user.setCoins(user.getCoins() - PACK_COST_IN_COINS);
		user.setAvailablePacks(user.getAvailablePacks() + 1);
		userRepo.save(user);
		transactionRepo.save(new Transaction(user, "PACK_BOUGHT", PACK_COST_IN_COINS, "Paquete comprado con monedas."));
	}

	public List<Transaction> getTransactionHistory(User user) {
		return transactionRepo.findByUserOrderByCreatedAtDesc(user);
	}

	private StickerResponseDTO createDTO(Sticker s, int duplicates) {
		return new StickerResponseDTO(s.getCode(), s.getTitle(), s.getSectionId(), s.getPageTitle(), s.getImageUrl(),
				true, duplicates, s.getRarity());
	}
}