package co.edu.unbosque.projectFifaUbosque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.projectFifaUbosque.model.Team;

import java.util.Optional;

/**
 * Interfaz de repositorio encargada del acceso a datos del catálogo de
 * selecciones oficiales de la Copa del Mundo.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

	/**
	 * Recupera la entidad de una selección basándose en su sigla corta ISO
	 * internacional de tres caracteres.
	 *
	 * @param shortName Sigla corta identificadora de la selección (ej: COL, ARG,
	 *                  BRA).
	 * @return Un {@link Optional} conteniendo la entidad {@link Team} si es
	 *         encontrada.
	 */
	public Optional<Team> findByShortName(String shortName);
}