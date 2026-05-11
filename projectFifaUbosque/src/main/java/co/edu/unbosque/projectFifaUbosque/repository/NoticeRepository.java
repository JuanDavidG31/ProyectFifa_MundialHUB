package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	List<Notice> findAllByOrderByCreatedAtDesc();
}