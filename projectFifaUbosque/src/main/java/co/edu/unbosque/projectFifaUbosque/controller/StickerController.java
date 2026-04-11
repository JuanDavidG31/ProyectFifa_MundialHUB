package co.edu.unbosque.projectFifaUbosque.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.projectFifaUbosque.model.Sticker;
import co.edu.unbosque.projectFifaUbosque.model.StickerType;
import co.edu.unbosque.projectFifaUbosque.service.StickerService;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/stickers")
@CrossOrigin(origins = "*")
public class StickerController {

	@Autowired
	private StickerService stickerService;

	@PostMapping(value = "/create", consumes = { "multipart/form-data" })
	public ResponseEntity<?> createSticker(@RequestParam("code") String code, @RequestParam("title") String title,
			@RequestParam("sectionId") String sectionId, @RequestParam("pageTitle") String pageTitle,
			@RequestParam("rarity") String rarity, @RequestParam("type") String type,
			@RequestParam("exchangeValue") Integer exchangeValue,
			@RequestParam(value = "imageUrl", required = false) String manualUrl,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			String finalUrl = manualUrl;

			if (imageFile != null && !imageFile.isEmpty()) {
				finalUrl = stickerService.uploadImage(imageFile);
			}

			Sticker newSticker = new Sticker(code, title, sectionId, pageTitle, finalUrl, rarity);
			newSticker.setType(StickerType.valueOf(type));
			newSticker.setExchangeValue(exchangeValue);

			return ResponseEntity.status(HttpStatus.CREATED).body(stickerService.createSticker(newSticker, finalUrl));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping(value = "/update/{id}", consumes = { "multipart/form-data" })
	public ResponseEntity<?> updateSticker(@PathVariable Long id, @RequestParam("title") String title,
			@RequestParam("sectionId") String sectionId, @RequestParam("pageTitle") String pageTitle,
			@RequestParam("rarity") String rarity, @RequestParam("type") String type,
			@RequestParam("exchangeValue") Integer exchangeValue,
			@RequestParam(value = "imageUrl", required = false) String manualUrl,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			String finalUrl = manualUrl;

			if (imageFile != null && !imageFile.isEmpty()) {
				finalUrl = stickerService.uploadImage(imageFile);
			}

			Sticker details = new Sticker();
			details.setTitle(title);
			details.setSectionId(sectionId);
			details.setPageTitle(pageTitle);
			details.setRarity(rarity);
			details.setType(StickerType.valueOf(type));
			details.setExchangeValue(exchangeValue);

			return ResponseEntity.ok(stickerService.updateSticker(id, details, finalUrl));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<Sticker>> getAllStickers() {
		List<Sticker> stickers = stickerService.getAllStickers();
		return ResponseEntity.ok(stickers);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getStickerById(@PathVariable Long id) {
		Optional<Sticker> sticker = stickerService.getStickerById(id);
		if (sticker.isPresent()) {
			return ResponseEntity.ok(sticker.get());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Sticker no encontrado"));
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteSticker(@PathVariable Long id) {
		try {
			stickerService.deleteSticker(id);
			return ResponseEntity.ok(Map.of("message", "Sticker eliminado exitosamente"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al eliminar el sticker"));
		}
	}
}