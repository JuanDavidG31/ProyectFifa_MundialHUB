package co.edu.unbosque.projectFifaUbosque.model;

/**
 * Enumerador encargado de definir el tipo o naturaleza de un Cromo
 * coleccionable.
 * <p>
 * Permite segmentar de manera lógica el comportamiento y renderizado en el
 * cliente para jugadores vigentes, escenarios o leyendas históricas del torneo.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public enum StickerType {
	/**
	 * Cromo correspondiente a un futbolista activo en las plantillas de las
	 * selecciones actuales.
	 */
	CURRENT_PLAYER,
	/** Cromo representativo de los estadios oficiales de la Copa del Mundo 2026. */
	STADIUM,
	/**
	 * Cromo de edición especial correspondiente a un futbolista histórico retirado
	 * de los mundiales.
	 */
	LEGEND
}