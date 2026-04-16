package co.edu.unbosque.projectFifaUbosque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.projectFifaUbosque.model.Team;

import java.util.Optional;
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
	public Optional<Team> findByShortName(String shortName);
}
