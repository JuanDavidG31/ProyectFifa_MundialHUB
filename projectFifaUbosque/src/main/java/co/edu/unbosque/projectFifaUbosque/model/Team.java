package co.edu.unbosque.projectFifaUbosque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad de persistencia que representa una Selección de Fútbol en la base de
 * datos.
 * <p>
 * Mapea las propiedades de las selecciones nacionales clasificadas al torneo,
 * incluyendo sus estadísticas de clasificación, puntos, ranking FIFA y zona de
 * grupos asignada.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "teams")
public class Team {

	/** Identificador único autogenerado en base de datos de la selección. */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/** Nombre oficial de la selección nacional. */
	private String teamName;

	/**
	 * Sigla o código ISO de tres caracteres que identifica de manera única a la
	 * selección (ej: COL, BRA).
	 */
	@Column(unique = true)
	private String shortName;

	/** URL o ruta del recurso de la imagen del escudo o bandera del país. */
	private String image;

	/** Nombre del país de la selección. */
	private String country;

	/** Letra correspondiente al grupo del mundial asignado a la selección. */
	@Enumerated(EnumType.STRING)
	private GroupName groupName;

	/**
	 * Posición oficial ocupada en la clasificación o ranking mundial de la FIFA.
	 */
	private int ranking;

	/** Cantidad de puntos acumulados en la clasificación oficial de la FIFA. */
	private int points;

	/**
	 * Enumerador que representa los doce grupos oficiales (de la A a la L) de la
	 * competición mundialista.
	 */
	public enum GroupName {
		A, B, C, D, E, F, G, H, I, J, K, L
	}

	/**
	 * Constructor por defecto para el uso del motor de persistencia de Hibernate.
	 */
	public Team() {
	}

	/**
	 * Constructor parametrizado con los campos esenciales para inicializar una
	 * selección.
	 *
	 * @param teamName  Nombre de la selección.
	 * @param shortName Sigla o abreviatura única.
	 * @param image     Ruta de la imagen de presentación.
	 * @param country   Nombre del país.
	 * @param groupName Grupo asignado.
	 * @param ranking   Posición en el ranking mundial.
	 * @param points    Puntos acumulados.
	 */
	public Team(String teamName, String shortName, String image, String country, GroupName groupName, int ranking,
			int points) {
		super();
		this.teamName = teamName;
		this.shortName = shortName;
		this.image = image;
		this.country = country;
		this.groupName = groupName;
		this.ranking = ranking;
		this.points = points;
	}

	/** @return El ID de la selección. */
	public Long getId() {
		return id;
	}

	/** @return El nombre oficial de la selección. */
	public String getTeamName() {
		return teamName;
	}

	/** @param teamName El nuevo nombre oficial de la selección. */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	/** @return La sigla o abreviatura única. */
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

	/** @param image El nuevo enlace de imagen a asignar. */
	public void setImage(String image) {
		this.image = image;
	}

	/** @return El nombre del país. */
	public String getCountry() {
		return country;
	}

	/** @param country El nuevo nombre de país a asignar. */
	public void setCountry(String country) {
		this.country = country;
	}

	/** @return El grupo oficial asignado en el torneo. */
	public GroupName getGroupName() {
		return groupName;
	}

	/** @param groupName El nuevo grupo a establecer. */
	public void setGroupName(GroupName groupName) {
		this.groupName = groupName;
	}

	/** @return La posición en el ranking FIFA. */
	public int getRanking() {
		return ranking;
	}

	/** @param ranking La nueva posición de ranking a asignar. */
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	/** @return Los puntos acumulados en la clasificación. */
	public int getPoints() {
		return points;
	}

	/** @param points Los nuevos puntos a establecer. */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Retorna una representación legible en texto de la entidad Team con sus campos
	 * deportivos.
	 *
	 * @return Cadena formateada de la selección de fútbol.
	 */
	@Override
	public String toString() {
		return "Team [teamName=" + teamName + ", shortName=" + shortName + ", image=" + image + ", country=" + country
				+ ", groupName=" + groupName + ", ranking=" + ranking + ", points=" + points + "]";
	}
}