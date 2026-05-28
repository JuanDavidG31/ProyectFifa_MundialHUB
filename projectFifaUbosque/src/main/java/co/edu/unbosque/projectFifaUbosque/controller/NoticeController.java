package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.model.Notice;
import co.edu.unbosque.projectFifaUbosque.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de administrar el tablón de noticias, novedades o
 * avisos oficiales de la plataforma.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")
public class NoticeController {
	/**
	 * Servicio encargado de la gestión de la persistencia y reglas de negocio de
	 * avisos.
	 */
	@Autowired
	private NoticeService noticeService;

	/**
	 * Recupera la totalidad de las noticias o avisos cargados en el sistema.
	 *
	 * @return Lista completa de entidades {@link Notice}.
	 */
	@GetMapping("/all")
	public List<Notice> getAll() {
		return noticeService.getAllNotices();
	}

	/**
	 * Publica o guarda una nueva noticia en el sistema.
	 *
	 * @param notice Objeto entidad con la información del aviso (título, contenido,
	 *               etc.).
	 * @return La entidad {@link Notice} guardada de forma definitiva.
	 */
	@PostMapping("/create")
	public Notice create(@RequestBody Notice notice) {
		return noticeService.saveNotice(notice);
	}

	/**
	 * Elimina una noticia o aviso del sistema mediante su identificador único.
	 *
	 * @param id Identificador único de la noticia.
	 * @return {@link ResponseEntity} indicando la finalización exitosa de la
	 *         eliminación.
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		noticeService.deleteNotice(id);
		return ResponseEntity.ok().build();
	}
}