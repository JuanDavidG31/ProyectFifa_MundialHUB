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

/**
 * Controlador REST exclusivo para la administración, creación, edición y
 * eliminación de láminas (Stickers) del Álbum.
 * <p>
 * Ofrece soporte híbrido para la creación multimedia por medio de carga de
 * archivos binarios hacia Cloudinary, o mediante asignaciones directas de URLs
 * textuales.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/stickers")
@CrossOrigin(origins = "*")
public class StickerController {
	/**
	 * Servicio encargado de orquestar la lógica transaccional de los
	 * coleccionables.
	 */
	@Autowired
	private StickerService stickerService;

	/**
	 * Crea un nuevo ítem o cromo coleccionable dentro del catálogo del álbum
	 * general.
	 * <p>
	 * Soporta la subida en formato multipart/form-data. Si se adjunta un archivo,
	 * este es subido de forma asíncrona a Cloudinary para poblar la URL final del
	 * sticker.
	 * </p>
	 *
	 * @param code          Código único identificador del cromo (Ej: ARG-01).
	 * @param title         Nombre del jugador o elemento ilustrado.
	 * @param sectionId     Categoría o sección a la que pertenece (Ej: selecciones,
	 *                      estadios).
	 * @param pageTitle     Nombre de la página específica.
	 * @param rarity        Rareza asignada (Común, Épica, Legendaria).
	 * @param type          Tipo de sticker según el enumerador base.
	 * @param exchangeValue Valor comercial en monedas internas para su canje.
	 * @param manualUrl     URL provista manualmente por el administrador
	 *                      (opcional).
	 * @param imageFile     Archivo binario de la imagen (opcional).
	 * @return {@link ResponseEntity} con el objeto {@link Sticker} persistido y
	 *         estado 201 Created, o error 500.
	 */
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

	/**
	 * Modifica los atributos de un sticker existente en el catálogo basándose en su
	 * ID único.
	 *
	 * @param id            Identificador numérico autogenerado del sticker.
	 * @param title         Nuevo nombre o título del cromo.
	 * @param sectionId     Nueva sección asignada.
	 * @param pageTitle     Nuevo título de página de visualización.
	 * @param rarity        Nueva rareza del sticker.
	 * @param type          Nuevo tipo según enumerador estructurado.
	 * @param exchangeValue Nuevo valor para intercambios.
	 * @param manualUrl     URL alternativa de imagen (opcional).
	 * @param imageFile     Nuevo archivo de imagen a reemplazar en Cloudinary
	 *                      (opcional).
	 * @return {@link ResponseEntity} con la entidad actualizada.
	 */
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

	/**
	 * Retorna la totalidad de las láminas registradas en el catálogo maestro del
	 * álbum digital.
	 *
	 * @return {@link ResponseEntity} con el listado de todos los cromos (Stickers).
	 */
	@GetMapping("/all")
	public ResponseEntity<List<Sticker>> getAllStickers() {
		List<Sticker> stickers = stickerService.getAllStickers();
		return ResponseEntity.ok(stickers);
	}

	/**
	 * Busca y retorna la información detallada de una lámina mediante su
	 * identificador numérico de clave primaria.
	 *
	 * @param id Identificador numérico del sticker.
	 * @return {@link ResponseEntity} con los datos del cromo o un estado 404 si no
	 *         se encuentra.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getStickerById(@PathVariable Long id) {
		Optional<Sticker> sticker = stickerService.getStickerById(id);
		if (sticker.isPresent()) {
			return ResponseEntity.ok(sticker.get());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Sticker no encontrado"));
		}
	}

	/**
	 * Suprime de manera lógica o física un sticker del catálogo maestro del
	 * sistema.
	 *
	 * @param id Identificador numérico del sticker a eliminar.
	 * @return {@link ResponseEntity} conteniendo un mapa explicativo del resultado
	 *         de la eliminación.
	 */
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