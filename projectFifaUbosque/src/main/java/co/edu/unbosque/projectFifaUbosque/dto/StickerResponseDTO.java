package co.edu.unbosque.projectFifaUbosque.dto;

/**
 * DTO de respuesta para representar el estado de una lámina (Sticker) en el
 * álbum del usuario.
 * <p>
 * Consolida los datos informativos del cromo junto con el estado de posesión
 * por parte del cliente y la cantidad de copias repetidas que posee para el
 * flujo de intercambios.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class StickerResponseDTO {

	/** Código único de inventario del cromo (ej: COL-10). */
	private String code;

	/** Título o nombre impreso en la lámina. */
	private String title;

	/** Identificador de la sección del álbum (ej: estadios, selecciones). */
	private String sectionId;

	/** Título de la página del álbum donde debe ser pegado. */
	private String pageTitle;

	/** URL absoluta de la imagen de la lámina alojada en Cloudinary. */
	private String imageUrl;

	/** Indicador de posesión (true si el usuario ya pegó la lámina en su álbum). */
	private boolean owned;

	/** Cantidad total de copias duplicadas disponibles para intercambio. */
	private int duplicates;

	/** Nivel de rareza de la lámina (Común, Épica, Legendaria). */
	private String rarity;

	/**
	 * Constructor completo para la respuesta del estado de láminas.
	 *
	 * @param code       Código del sticker.
	 * @param title      Nombre o título.
	 * @param sectionId  ID de sección.
	 * @param pageTitle  Título de página.
	 * @param imageUrl   URL del recurso visual.
	 * @param owned      Estado de posesión.
	 * @param duplicates Cantidad de repetidas.
	 * @param rarity     Grado de rareza.
	 */
	public StickerResponseDTO(String code, String title, String sectionId, String pageTitle, String imageUrl,
			boolean owned, int duplicates, String rarity) {
		super();
		this.code = code;
		this.title = title;
		this.sectionId = sectionId;
		this.pageTitle = pageTitle;
		this.imageUrl = imageUrl;
		this.owned = owned;
		this.duplicates = duplicates;
		this.rarity = rarity;
	}

	/** @return El código único del sticker. */
	public String getCode() {
		return code;
	}

	/** @param code El nuevo código a asignar. */
	public void setCode(String code) {
		this.code = code;
	}

	/** @return El título o nombre de la lámina. */
	public String getTitle() {
		return title;
	}

	/** @param title El nuevo título a asignar. */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return El ID de la sección del álbum. */
	public String getSectionId() {
		return sectionId;
	}

	/** @param sectionId El nuevo ID de sección a asignar. */
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	/** @return El título de la página del álbum. */
	public String getPageTitle() {
		return pageTitle;
	}

	/** @param pageTitle El nuevo título de página a asignar. */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/** @return La URL de la imagen en la nube. */
	public String getImageUrl() {
		return imageUrl;
	}

	/** @param imageUrl La nueva URL de imagen a asignar. */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/** @return true si la lámina ya pertenece a la colección pegada del usuario. */
	public boolean isOwned() {
		return owned;
	}

	/** @param owned El nuevo estado de posesión a asignar. */
	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	/** @return El número de copias duplicadas. */
	public int getDuplicates() {
		return duplicates;
	}

	/** @param duplicates La nueva cantidad de duplicados a asignar. */
	public void setDuplicates(int duplicates) {
		this.duplicates = duplicates;
	}

	/** @return La rareza de la pieza. */
	public String getRarity() {
		return rarity;
	}

	/** @param rarity La nueva rareza a asignar. */
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
}