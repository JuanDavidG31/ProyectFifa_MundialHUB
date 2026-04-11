package co.edu.unbosque.projectFifaUbosque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.projectFifaUbosque.model.Sticker;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
	boolean existsByCode(String code);
}