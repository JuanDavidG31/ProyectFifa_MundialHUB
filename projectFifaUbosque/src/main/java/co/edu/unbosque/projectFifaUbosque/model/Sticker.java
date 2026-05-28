package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.*;

/**
 * Entidad de persistencia que representa un Cromo o Lámina coleccionable del
 * álbum digital.
 * <p>
 * Define los atributos lógicos de los stickers del catálogo maestro,
 * controlando su nivel de rareza, el título de la página del álbum donde se
 * debe posicionar y el valor interno para procesos de canje.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "stickers")
public class Sticker {

	/** Llave primaria autoincremental de la lámina en la base de datos. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Código único alfanumérico que identifica el cromo dentro del álbum (ej:
	 * BRA-10).
	 */
	@Column(unique = true, nullable = false)
	private String code;

	/**
	 * Título o nombre impreso en la lámina, correspondiente al jugador o elemento
	 * representativo.
	 */
	@Column(nullable = false)
	private String title;

	/** Identificador único de la sección temática a la que pertenece la lámina. */
	@Column(nullable = false)
	private String sectionId;

	/**
	 * Nombre o título de la página específica del álbum físico/digital donde debe
	 * pegarse.
	 */
	@Column(nullable = false)
	private String pageTitle;

	/**
	 * URL absoluta de la imagen multimedia de la lámina alojada de manera segura en
	 * la nube.
	 */
	private String imageUrl;

	/** Grado de rareza de la lámina (por defecto: "Común"). */
	@Column(nullable = false)
	private String rarity = "Común";

	/**
	 * Clasificación estructural de la estampa (Jugador, Estadio, Leyenda) evaluada
	 * mediante un enumerador.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StickerType type = StickerType.CURRENT_PLAYER;

	/**
	 * Valor neto en monedas de la plataforma asignado al cromo para cuando sea
	 * vendido o canjeado por duplicado.
	 */
	@Column(name = "exchange_value", nullable = false)
	private int exchangeValue = 10;

	/**
	 * Constructor por defecto indispensable para la gestión transaccional de JPA.
	 */
	public Sticker() {
	}

	/**
	 * Constructor full de campos para inicializar lógicas o precargas de láminas en
	 * el sistema.
	 *
	 * @param code      Código identificador único.
	 * @param title     Nombre del jugador o elemento.
	 * @param sectionId Identificador de la sección.
	 * @param pageTitle Título de la página del álbum.
	 * @param imageUrl  Enlace multimedia de la imagen.
	 * @param rarity    Nivel de rareza.
	 */
	public Sticker(String code, String title, String sectionId, String pageTitle, String imageUrl, String rarity) {
		this.code = code;
		this.title = title;
		this.sectionId = sectionId;
		this.pageTitle = pageTitle;
		this.imageUrl = imageUrl;
		this.rarity = rarity;
	}

	/** @return El identificador de la lámina. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de persistencia a establecer. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El código alfanumérico único. */
	public String getCode() {
		return code;
	}

	/** @param code El nuevo código único a asignar. */
	public void setCode(String code) {
		this.code = code;
	}

	/** @return El nombre o título impreso. */
	public String getTitle() {
		return title;
	}

	/** @param title El nuevo título o nombre a asignar. */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return El ID de la sección. */
	public String getSectionId() {
		return sectionId;
	}

	/** @param sectionId El nuevo ID de sección a configurar. */
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	/** @return El tipo estructural de cromo. */
	public StickerType getType() {
		return type;
	}

	/** @param type El nuevo tipo de la lámina a establecer. */
	public void setType(StickerType type) {
		this.type = type;
	}

	/** @return El valor de reembolso en monedas por duplicado. */
	public int getExchangeValue() {
		return exchangeValue;
	}

	/** @param exchangeValue El nuevo valor de canje en monedas. */
	public void setExchangeValue(int exchangeValue) {
		this.exchangeValue = exchangeValue;
	}

	/** @return El título de la página del álbum. */
	public String getPageTitle() {
		return pageTitle;
	}

	/** @param pageTitle El nuevo título de página a establecer. */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/** @return El enlace a la imagen en la nube. */
	public String getImageUrl() {
		return imageUrl;
	}

	/** @param imageUrl El nuevo enlace de la imagen multimedia. */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/** @return La rareza asignada a la pieza. */
	public String getRarity() {
		return rarity;
	}

	/** @param rarity El nuevo grado de rareza a configurar. */
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
}