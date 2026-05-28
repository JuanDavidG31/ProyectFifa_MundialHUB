package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de persistencia que representa un Reporte de Auditoría o Reclamación
 * por un paquete de láminas.
 * <p>
 * Mapea las incidencias, fallas de apertura de sobres o inconsistencias en los
 * cobros financieros radicadas formalmente por un usuario ante el servicio de
 * soporte técnico.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "package_reports")
public class PackageReport {

	/** Clave primaria autoincremental del ticket de reporte. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Correo electrónico del usuario afectado que genera el reclamo de soporte. */
	private String userEmail;

	/** Nombre comercial del paquete virtual de láminas involucrado en el fallo. */
	private String packageName;

	/** Tipo o categoría de sobre que originó el reporte (Premium, Estándar). */
	private String packageType;

	/**
	 * Registro temporal con la fecha y hora exactas en las cuales se adquirió el
	 * producto.
	 */
	private LocalDateTime purchaseDate;

	/**
	 * Constructor por defecto para la correcta inicialización relacional de JPA.
	 */
	public PackageReport() {
	}

	/** @return Identificador del reporte. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de persistencia a establecer. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El correo del usuario afectado. */
	public String getUserEmail() {
		return userEmail;
	}

	/** @param userEmail El nuevo correo de usuario a configurar. */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/** @return El nombre comercial del paquete virtual. */
	public String getPackageName() {
		return packageName;
	}

	/** @param packageName El nuevo nombre del paquete a establecer. */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/** @return La categoría del sobre. */
	public String getPackageType() {
		return packageType;
	}

	/** @param packageType El nuevo tipo de sobre a asignar. */
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	/** @return La fecha y hora de la compra reportada. */
	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}

	/** @param purchaseDate La nueva fecha de compra a asociar en el reclamo. */
	public void setPurchaseDate(LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
}