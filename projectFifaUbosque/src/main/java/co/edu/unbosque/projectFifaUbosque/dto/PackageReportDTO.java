package co.edu.unbosque.projectFifaUbosque.dto;

import java.time.LocalDateTime;

/**
 * Objeto de Transferencia de Datos (DTO) para la gestión y reporte de
 * incidencias en paquetes de láminas.
 * <p>
 * Transmite los datos consolidados sobre la compra de sobres o paquetes
 * comerciales que presenten anomalías o requieran auditoría por parte de
 * soporte técnico.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class PackageReportDTO {

	/** Correo electrónico del usuario que radica el reporte. */
	private String userEmail;

	/** Nombre comercial del paquete adquirido. */
	private String packageName;

	/** Categoría o tipo de paquete (ej: Premium, Estándar, Especial). */
	private String packageType;

	/** Fecha y hora exacta en la que se efectuó la transacción de compra. */
	private LocalDateTime purchaseDate;

	/**
	 * @return El correo electrónico del usuario.
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail El nuevo correo electrónico a asignar.
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @return El nombre del paquete.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @param packageName El nuevo nombre del paquete a asignar.
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @return El tipo o categoría del paquete.
	 */
	public String getPackageType() {
		return packageType;
	}

	/**
	 * @param packageType El nuevo tipo de paquete a asignar.
	 */
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	/**
	 * @return La fecha y hora de la compra.
	 */
	public LocalDateTime getPurchaseDate() {
		return purchaseDate;
	}

	/**
	 * @param purchaseDate La nueva fecha de compra a asignar.
	 */
	public void setPurchaseDate(LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
}