package co.edu.unbosque.projectFifaUbosque.dto;

/**
 * Objeto de Transferencia de Datos (DTO) para las estadísticas de rendimiento
 * de jugadores.
 * <p>
 * Utilizado para estructurar y transferir ránkings del torneo en tiempo real,
 * tales como tablas de máximos goleadores o líderes de asistencias.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class PlayerStatDTO {

	/** Nombre completo del deportista. */
	private String name;

	/** Nombre del equipo o selección nacional a la que representa. */
	private String team;

	/** URL de la imagen de la bandera de la selección del jugador. */
	private String flagUrl;

	/**
	 * Valor cuantitativo de la estadística acumulada (ej: número de goles o
	 * asistencias).
	 */
	private int value;

	/**
	 * Constructor con todos los campos obligatorios para las estadísticas de
	 * jugadores.
	 *
	 * @param name    Nombre del jugador.
	 * @param team    Selección o equipo.
	 * @param flagUrl Enlace al recurso de la bandera.
	 * @param value   Métrica numérica alcanzada.
	 */
	public PlayerStatDTO(String name, String team, String flagUrl, int value) {
		this.name = name;
		this.team = team;
		this.flagUrl = flagUrl;
		this.value = value;
	}

	/**
	 * @return El nombre del jugador.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name El nuevo nombre a asignar.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return El equipo o selección nacional.
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * @param team El nuevo equipo a asignar.
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/**
	 * @return La URL de la bandera.
	 */
	public String getFlagUrl() {
		return flagUrl;
	}

	/**
	 * @param flagUrl La nueva URL de la bandera a asignar.
	 */
	public void setFlagUrl(String flagUrl) {
		this.flagUrl = flagUrl;
	}

	/**
	 * @return El valor numérico de la estadística.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value El nuevo valor estadístico a asignar.
	 */
	public void setValue(int value) {
		this.value = value;
	}
}