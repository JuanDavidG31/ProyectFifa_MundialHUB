package co.edu.unbosque.projectFifaUbosque.controller;

import co.edu.unbosque.projectFifaUbosque.dto.PackageReportDTO;
import co.edu.unbosque.projectFifaUbosque.model.PackageReport;
import co.edu.unbosque.projectFifaUbosque.service.PackageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de la creación y consulta de reportes de
 * quejas/sugerencias sobre paquetes turísticos o comerciales.
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class PackageReportController {
	/** Servicio lógico encargado del procesamiento de reportes. */
	@Autowired
	private PackageReportService reportService;

	/**
	 * Registra y guarda un nuevo reporte o incidencia enviada por el usuario.
	 *
	 * @param reportDto DTO estructurado con la información y justificación del
	 *                  reporte.
	 * @return {@link ResponseEntity} con un mensaje confirmando el éxito del
	 *         almacenamiento.
	 */
	@PostMapping("/save")
	public ResponseEntity<String> saveReport(@RequestBody PackageReportDTO reportDto) {
		reportService.saveReport(reportDto);
		return ResponseEntity.ok("Reporte guardado exitosamente");
	}

	/**
	 * Recupera el listado de reportes históricos radicados por un usuario mediante
	 * su correo electrónico.
	 *
	 * @param email Correo electrónico del usuario consultante.
	 * @return {@link ResponseEntity} con la lista de entidades
	 *         {@link PackageReport} encontradas.
	 */
	@GetMapping("/user")
	public ResponseEntity<List<PackageReport>> getUserReports(@RequestParam String email) {
		return ResponseEntity.ok(reportService.getReportsByUser(email));
	}
}