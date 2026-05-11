package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.ItineraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItineraryEventRepository extends JpaRepository<ItineraryEvent, Long> {

	List<ItineraryEvent> findByUserEmail(String userEmail);
	
	@Transactional
    void deleteByUserEmail(String userEmail);
}