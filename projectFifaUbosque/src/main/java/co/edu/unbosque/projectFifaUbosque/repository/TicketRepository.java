package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio encargada de administrar la persistencia de las
 * entradas digitales de partidos.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	/**
	 * Busca una entrada digital única basándose en su código alfanumérico universal
	 * UUID.
	 *
	 * @param uuid Código universal único del ticket.
	 * @return Un contenedor {@link Optional} con el ticket correspondiente.
	 */
	Optional<Ticket> findByUuid(String uuid);

	/**
	 * Recupera todas las boletas o reservas adquiridas históricamente por un
	 * usuario específico.
	 *
	 * @param userEmail Correo electrónico del hincha comprador.
	 * @return Lista completa de objetos {@link Ticket} asociados al correo.
	 */
	List<Ticket> findByUserEmail(String userEmail);

	/**
	 * Cuenta la cantidad acumulada de entradas vendidas para un partido específico
	 * para controlar el aforo máximo del estadio.
	 *
	 * @param matchName Nombre descriptivo del partido (ej: "México vs Alemania").
	 * @return Recuento total cuantitativo de boletas expedidas.
	 */
	long countByMatchName(String matchName);

	/**
	 * Consulta estadística estructurada diseñada para agrupar y contar la totalidad
	 * de las boletas vendidas por cada partido.
	 *
	 * @return Lista de arreglos de objetos, donde el índice 0 contiene el nombre
	 *         del partido y el índice 1 el conteo cuantitativo.
	 */
	@Query("SELECT t.matchName, COUNT(t) FROM Ticket t GROUP BY t.matchName")
	List<Object[]> countAllTicketsGroupedByMatch();

	/**
	 * Operación transaccional modificadora diseñada para eliminar de forma masiva
	 * los registros de boletería de un usuario.
	 *
	 * @param userEmail Correo electrónico del usuario a purgar.
	 */
	@Transactional
	void deleteByUserEmail(String userEmail);
}