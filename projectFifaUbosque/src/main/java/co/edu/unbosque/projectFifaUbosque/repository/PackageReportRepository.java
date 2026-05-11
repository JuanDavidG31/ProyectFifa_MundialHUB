package co.edu.unbosque.projectFifaUbosque.repository;

import co.edu.unbosque.projectFifaUbosque.model.PackageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PackageReportRepository extends JpaRepository<PackageReport, Long> {
	List<PackageReport> findByUserEmailOrderByPurchaseDateDesc(String userEmail);
	@Modifying
	@Transactional
    void deleteByUserEmail(String userEmail);
}