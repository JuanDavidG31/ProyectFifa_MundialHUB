package co.edu.unbosque.projectFifaUbosque.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.projectFifaUbosque.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByUser(String user);

	public void deleteByUser(String user);

	public Optional<User> findByPersonalId(String personalId);

	public Optional<User> findByAvatar(String avatar);

	public Optional<User> findByEmail(String email);
}
