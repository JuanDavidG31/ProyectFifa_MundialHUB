package co.edu.unbosque.projectFifaUbosque.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.projectFifaUbosque.model.Transaction;
import co.edu.unbosque.projectFifaUbosque.model.User;
import jakarta.transaction.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	
	@Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :user AND t.transactionType = :type AND t.createdAt >= :startDate")
	long countTransactionsByUserAndTypeSince(@Param("user") User user, @Param("type") String type,
			@Param("startDate") LocalDateTime startDate);

	List<Transaction> findByUserOrderByCreatedAtDesc(User user);

	@Modifying
	@Transactional
	void deleteByUser(User user);
}