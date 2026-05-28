package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de persistencia encargada de modelar un Comunicado Oficial, Noticia o
 * Alerta de la plataforma.
 * <p>
 * Mapea los artículos informativos publicados por la administración para
 * mantener actualizados a los usuarios acerca de novedades del mundial,
 * partidos, o contingencias logísticas de última hora.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "notices")
public class Notice {

	/**
	 * Identificador único autoincremental de la noticia en los registros de base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Título informativo o encabezado de la noticia. */
	private String title;

	/**
	 * Cuerpo de texto central que detalla el comunicado emitido, ampliado hasta
	 * 2000 caracteres de capacidad.
	 */
	@Column(length = 2000)
	private String content;

	/**
	 * URL absoluta de la imagen ilustrativa o miniatura del boletín de noticias.
	 */
	private String imageUrl;

	/**
	 * Marca temporal con la fecha y hora exacta en la que se generó e introdujo la
	 * noticia al portal.
	 */
	private LocalDateTime createdAt;

	/**
	 * Constructor por defecto indispensable para la inicialización en consultas
	 * nativas y relacionales.
	 */
	public Notice() {
	}

	/**
	 * Constructor completo para inicializaciones directas de la entidad de
	 * noticias.
	 *
	 * @param id        ID único.
	 * @param title     Título del comunicado.
	 * @param content   Cuerpo informativo extendido.
	 * @param imageUrl  Enlace multimedia de la miniatura.
	 * @param createdAt Marca temporal de publicación.
	 */
	public Notice(Long id, String title, String content, String imageUrl, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
	}

	/** @return Identificador numérico de la noticia. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de persistencia a asignar. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El título de la noticia. */
	public String getTitle() {
		return title;
	}

	/** @param title El nuevo título o encabezado a establecer. */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return El contenido extendido. */
	public String getContent() {
		return content;
	}

	/** @param content El nuevo cuerpo central a asignar. */
	public void setContent(String content) {
		this.content = content;
	}

	/** @return La URL de la imagen en la nube. */
	public String getImageUrl() {
		return imageUrl;
	}

	/** @param imageUrl El nuevo enlace de miniatura multimedia a configurar. */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/** @return La fecha y hora de publicación. */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/** @param createdAt La nueva marca temporal de creación a asignar. */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Genera una representación textual formateada de la entidad Notice y sus
	 * campos lógicos.
	 *
	 * @return Cadena estructurada del aviso.
	 */
	@Override
	public String toString() {
		return "Notice [id=" + id + ", title=" + title + ", content=" + content + ", imageUrl=" + imageUrl
				+ ", createdAt=" + createdAt + "]";
	}
}