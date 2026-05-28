package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.ItineraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interfaz de repositorio encargada de gestionar el almacenamiento de las
 * agendas e itinerarios de viaje.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Repository
public interface ItineraryEventRepository extends JpaRepository<ItineraryEvent, Long> {

	/**
	 * Recupera el listado completo de actividades agendadas por un hincha mediante
	 * su cuenta de correo electrónico.
	 *
	 * @param userEmail Correo del usuario propietario del itinerario.
	 * @return Lista de eventos de tipo {@link ItineraryEvent}.
	 */
	List<ItineraryEvent> findByUserEmail(String userEmail);

	/**
	 * Remueve de forma definitiva todas las actividades lógicas guardadas en el
	 * itinerario de un usuario.
	 *
	 * @param userEmail Correo del usuario a limpiar.
	 */
	@Transactional
	void deleteByUserEmail(String userEmail);
}