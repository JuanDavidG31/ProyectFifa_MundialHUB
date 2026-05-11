package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.PackageReportDTO;
import co.edu.unbosque.projectFifaUbosque.model.PackageReport;
import co.edu.unbosque.projectFifaUbosque.service.PackageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class PackageReportController {

	@Autowired
	private PackageReportService reportService;

	@PostMapping("/save")
	public ResponseEntity<String> saveReport(@RequestBody PackageReportDTO reportDto) {
		reportService.saveReport(reportDto);
		return ResponseEntity.ok("Reporte guardado exitosamente");
	}

	@GetMapping("/user")
	public ResponseEntity<List<PackageReport>> getUserReports(@RequestParam String email) {
		return ResponseEntity.ok(reportService.getReportsByUser(email));
	}
}