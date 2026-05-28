package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.PackageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interfaz de repositorio encargada de la persistencia y consultas
 * operacionales de reportes de quejas.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public interface PackageReportRepository extends JpaRepository<PackageReport, Long> {

	/**
	 * Consulta las incidencias y reclamaciones radicadas por un usuario ordenadas
	 * de forma cronológica descendente.
	 *
	 * @param userEmail Correo electrónico del usuario consultante.
	 * @return Lista de entidades {@link PackageReport} ordenadas por fecha.
	 */
	List<PackageReport> findByUserEmailOrderByPurchaseDateDesc(String userEmail);

	/**
	 * Operación modificadora diseñada para purgar de la base de datos todos los
	 * reportes asociados a una cuenta.
	 *
	 * @param userEmail Correo electrónico del usuario a limpiar.
	 */
	@Modifying
	@Transactional
	void deleteByUserEmail(String userEmail);
}