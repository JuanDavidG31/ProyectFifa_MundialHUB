package co.edu.unbosque.projectFifaUbosque.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.projectFifaUbosque.model.Transaction;
import co.edu.unbosque.projectFifaUbosque.model.User;
import jakarta.transaction.Transactional;

/**
 * Interfaz de repositorio encargada de la persistencia del historial financiero
 * del sistema.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	/**
	 * Cuenta la cantidad de transacciones de un tipo específico realizadas por un
	 * usuario desde una fecha determinada.
	 * <p>
	 * Utilizada principalmente para aplicar reglas de negocio o controles
	 * anti-fraude diarios en reclamos de recompensas.
	 * </p>
	 *
	 * @param user      Entidad {@link User} objetivo de auditoría.
	 * @param type      Cadena con el tipo de transacción a filtrar.
	 * @param startDate Fecha límite inferior de la búsqueda.
	 * @return Recuento total de operaciones detectadas.
	 */
	@Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :user AND t.transactionType = :type AND t.createdAt >= :startDate")
	long countTransactionsByUserAndTypeSince(@Param("user") User user, @Param("type") String type,
			@Param("startDate") LocalDateTime startDate);

	/**
	 * Consulta el extracto o historial completo de movimientos de un usuario
	 * ordenados del más reciente al más antiguo.
	 *
	 * @param user Entidad del usuario consultante.
	 * @return Lista ordenada cronológicamente de objetos {@link Transaction}.
	 */
	List<Transaction> findByUserOrderByCreatedAtDesc(User user);

	/**
	 * Elimina de manera definitiva todo el historial de transacciones financieras
	 * de un usuario.
	 *
	 * @param user Entidad del usuario a limpiar.
	 */
	@Modifying
	@Transactional
	void deleteByUser(User user);
}