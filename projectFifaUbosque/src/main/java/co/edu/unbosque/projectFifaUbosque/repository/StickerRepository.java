package co.edu.unbosque.projectFifaUbosque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.projectFifaUbosque.model.Sticker;

/**
 * Interfaz de repositorio encargada de la persistencia del catálogo maestro de
 * cromos (Stickers).
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {

	/**
	 * Verifica de forma idempotente la existencia de una lámina basándose en su
	 * código alfanumérico único de álbum.
	 *
	 * @param code Código único de la lámina (ej: COL-10).
	 * @return true si la lámina ya se encuentra registrada en el catálogo maestro.
	 */
	boolean existsByCode(String code);
}