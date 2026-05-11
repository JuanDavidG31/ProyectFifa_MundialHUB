package co.edu.unbosque.projectFifaUbosque.service;

import co.edu.unbosque.projectFifaUbosque.dto.PackageReportDTO;
import co.edu.unbosque.projectFifaUbosque.model.PackageReport;
import co.edu.unbosque.projectFifaUbosque.repository.PackageReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PackageReportService {

	@Autowired
	private PackageReportRepository reportRepo;

	public void saveReport(PackageReportDTO dto) {
		PackageReport report = new PackageReport();
		report.setUserEmail(dto.getUserEmail());
		report.setPackageName(dto.getPackageName());
		report.setPackageType(dto.getPackageType());
		report.setPurchaseDate(LocalDateTime.now());

		reportRepo.save(report);
	}

	public List<PackageReport> getReportsByUser(String email) {
		return reportRepo.findByUserEmailOrderByPurchaseDateDesc(email);
	}
}