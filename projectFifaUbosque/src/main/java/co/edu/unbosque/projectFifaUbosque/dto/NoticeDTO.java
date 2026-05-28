package co.edu.unbosque.projectFifaUbosque.dto;

import java.time.LocalDateTime;

/**
 * Objeto de Transferencia de Datos (DTO) para la visualización y publicación de
 * noticias e informativos.
 * <p>
 * Mapea los boletines de novedades, avisos de seguridad de aforo en estadios o
 * contingencias de última hora emitidas por el comité organizador para los
 * usuarios del portal.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class NoticeDTO {

	/** Identificador único numérico de la noticia. */
	private Long id;

	/** Título o encabezado principal del aviso informativo. */
	private String title;

	/** Contenido detallado o cuerpo central de la noticia. */
	private String content;

	/** URL de la imagen ilustrativa o miniatura del boletín de noticias. */
	private String imageUrl;

	/** Fecha y hora exacta en la que se publicó el comunicado al sistema. */
	private LocalDateTime createdAt;

	/**
	 * Constructor por defecto para la inicialización y mapeo de datos.
	 */
	public NoticeDTO() {
		// Constructor base vacío de la sección de avisos.
	}

	/** @return El ID único del aviso. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID a asignar. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El título del aviso informativo. */
	public String getTitle() {
		return title;
	}

	/** @param title El nuevo título a asignar. */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return El contenido detallado. */
	public String getContent() {
		return content;
	}

	/** @param content El nuevo texto a asignar. */
	public void setContent(String content) {
		this.content = content;
	}

	/** @return La URL de la imagen del aviso. */
	public String getImageUrl() {
		return imageUrl;
	}

	/** @param imageUrl La nueva URL de imagen a asignar. */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/** @return La fecha de creación del registro. */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/** @param createdAt La nueva fecha de creación a asignar. */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}