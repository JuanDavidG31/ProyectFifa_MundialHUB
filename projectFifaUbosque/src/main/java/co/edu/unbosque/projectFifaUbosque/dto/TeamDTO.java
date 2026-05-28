package co.edu.unbosque.projectFifaUbosque.dto;

import co.edu.unbosque.projectFifaUbosque.model.Team.GroupName;

/**
 * Objeto de Transferencia de Datos (DTO) para la representación de las
 * Selecciones de Fútbol.
 * <p>
 * Encapsula la metadata técnica y deportiva de un equipo participante en el
 * Mundial 2026, incluyendo su grupo asignado, ránkings y puntajes en la
 * clasificación FIFA.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class TeamDTO {

	/** Identificador único autogenerado en base de datos. */
	private Long id;

	/** Nombre oficial de la selección (ej: Colombia). */
	private String teamName;

	/** Sigla o nombre corto de la selección (ej: COL). */
	private String shortName;

	/** URL o ruta del escudo o bandera del equipo. */
	private String image;

	/** País al que pertenece. */
	private String country;

	/** Grupo del mundial asignado mediante el enumerador de zonas (A-L). */
	private GroupName groupName;

	/** Puesto oficial ocupado en el ránking mundial FIFA. */
	private int ranking;

	/** Puntos acumulados en la clasificación oficial. */
	private int points;

	/**
	 * Constructor por defecto para la inicialización serializable.
	 */
	public TeamDTO() {
		// Constructor base vacío.
	}

	/**
	 * Constructor con todos los campos para la creación estructurada de DTOs de
	 * equipos.
	 *
	 * @param id        ID único.
	 * @param teamName  Nombre de la selección.
	 * @param shortName Formato corto / Sigla.
	 * @param image     Ruta del escudo.
	 * @param country   País origen.
	 * @param groupName Grupo del mundial.
	 * @param ranking   Puesto en el ránking.
	 * @param points    Puntaje FIFA.
	 */
	public TeamDTO(Long id, String teamName, String shortName, String image, String country, GroupName groupName,
			int ranking, int points) {
		super();
		this.id = id;
		this.teamName = teamName;
		this.shortName = shortName;
		this.image = image;
		this.country = country;
		this.groupName = groupName;
		this.ranking = ranking;
		this.points = points;
	}

	/** @return El ID del equipo. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID a asignar. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El nombre de la selección. */
	public String getTeamName() {
		return teamName;
	}

	/** @param teamName El nuevo nombre de selección a asignar. */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	/** @return El nombre corto o sigla. */
	public String getShortName() {
		return shortName;
	}

	/** @param shortName La nueva sigla a asignar. */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/** @return La ruta o enlace de la imagen del escudo. */
	public String getImage() {
		return image;
	}

	/** @param image La nueva ruta de imagen a asignar. */
	public void setImage(String image) {
		this.image = image;
	}

	/** @return El país de procedencia. */
	public String getCountry() {
		return country;
	}

	/** @param country El nuevo país a asignar. */
	public void setCountry(String country) {
		this.country = country;
	}

	/** @return El grupo del mundial asignado. */
	public GroupName getGroupName() {
		return groupName;
	}

	/** @param groupName El nuevo grupo a asignar. */
	public void setGroupName(GroupName groupName) {
		this.groupName = groupName;
	}

	/** @return El puesto en el ránking FIFA. */
	public int getRanking() {
		return ranking;
	}

	/** @param ranking El nuevo puesto a asignar. */
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	/** @return Los puntos acumulados. */
	public int getPoints() {
		return points;
	}

	/** @param points Los nuevos puntos a asignar. */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Retorna una representación en cadena de texto con los valores de los
	 * atributos del equipo.
	 *
	 * @return Cadena formateada de TeamDTO.
	 */
	@Override
	public String toString() {
		return "TeamDTO [id=" + id + ", teamName=" + teamName + ", shortName=" + shortName + ", image=" + image
				+ ", country=" + country + ", groupName=" + groupName + ", ranking=" + ranking + ", points=" + points
				+ "]";
	}
}