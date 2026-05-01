package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	Optional<Ticket> findByUuid(String uuid);

	List<Ticket> findByUserEmail(String userEmail);
	
	long countByMatchName(String matchName);
	
	@Query("SELECT t.matchName, COUNT(t) FROM Ticket t GROUP BY t.matchName")
	List<Object[]> countAllTicketsGroupedByMatch();
	
	@Transactional
    void deleteByUserEmail(String userEmail);
}