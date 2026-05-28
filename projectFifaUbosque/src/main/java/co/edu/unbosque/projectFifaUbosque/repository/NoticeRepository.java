package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Interfaz de repositorio encargada de la persistencia de las noticias y
 * alertas del portal informativo.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	/**
	 * Consulta el tablón completo de boletines informativos ordenados de manera
	 * cronológica inversa para destacar las novedades de última hora.
	 *
	 * @return Lista de entidades {@link Notice} ordenadas por su fecha de creación
	 *         (`createdAt`) de forma descendente.
	 */
	List<Notice> findAllByOrderByCreatedAtDesc();
}