package co.edu.unbosque.projectFifaUbosque.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.projectFifaUbosque.model.User;
import co.edu.unbosque.projectFifaUbosque.model.UserSticker;
import jakarta.transaction.Transactional;

@Repository
public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {

	
	List<UserSticker> findByUser(User user);

	
	Optional<UserSticker> findByUserAndSticker_Code(User user, String code);

	@Modifying
	@Transactional
	void deleteByUser(User user);
}