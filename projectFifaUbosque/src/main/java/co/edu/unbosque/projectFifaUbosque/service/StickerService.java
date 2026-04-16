package co.edu.unbosque.projectFifaUbosque.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import co.edu.unbosque.projectFifaUbosque.model.Sticker;
import co.edu.unbosque.projectFifaUbosque.repository.StickerRepository;

@Service
public class StickerService {

	@Autowired
	private StickerRepository stickerRepo;

	@Autowired
	private Cloudinary cloudinary;

	public String uploadImage(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
		}

		Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

		return (String) uploadResult.get("secure_url");
	}

	public Sticker createSticker(Sticker sticker, String imageUrl) {
		if (stickerRepo.existsByCode(sticker.getCode())) {
			throw new IllegalArgumentException("Ya existe un sticker con el código: " + sticker.getCode());
		}

		sticker.setImageUrl(imageUrl);
		return stickerRepo.save(sticker);
	}

	public List<Sticker> getAllStickers() {
		return stickerRepo.findAll();
	}

	public Optional<Sticker> getStickerById(Long id) {
		return stickerRepo.findById(id);
	}

	public Sticker updateSticker(Long id, Sticker updatedSticker, String imageUrl) {
		return stickerRepo.findById(id).map(existingSticker -> {
			existingSticker.setTitle(updatedSticker.getTitle());
			existingSticker.setSectionId(updatedSticker.getSectionId());
			existingSticker.setPageTitle(updatedSticker.getPageTitle());
			existingSticker.setRarity(updatedSticker.getRarity());
			existingSticker.setType(updatedSticker.getType());
			existingSticker.setExchangeValue(updatedSticker.getExchangeValue());

			if (imageUrl != null && !imageUrl.isEmpty()) {
				existingSticker.setImageUrl(imageUrl);
			}

			return stickerRepo.save(existingSticker);
		}).orElseThrow(() -> new RuntimeException("Sticker no encontrado con el ID: " + id));
	}

	public void deleteSticker(Long id) {
		if (!stickerRepo.existsById(id)) {
			throw new RuntimeException("Sticker no encontrado con el ID: " + id);
		}
		stickerRepo.deleteById(id);
	}
}