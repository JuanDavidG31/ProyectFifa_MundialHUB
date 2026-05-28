package co.edu.unbosque.projectFifaUbosque.dto;

/**
 * Objeto de Transferencia de Datos (DTO) para la visualización de Partidos
 * aptos para venta de Boletería.
 * <p>
 * Estructura y transfiere la información de disponibilidad de partidos desde el
 * backend hacia las interfaces web de taquilla y compra de entradas.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class TicketMatchDTO {

	/**
	 * Identificador numérico único del partido asignado en los catálogos
	 * deportivos.
	 */
	private Long id;

	/** Nombre de la selección nacional que compite en calidad de Local. */
	private String local;

	/** Nombre de la selección nacional que compite en calidad de Visitante. */
	private String visitante;

	/**
	 * Formato textual estructurado con la programación de fecha y hora del
	 * encuentro.
	 */
	private String fecha;

	/** Nombre o denominación del recinto o estadio oficial sede del partido. */
	private String estadio;

	/**
	 * Costo comercial o precio base de la entrada en monedas de la plataforma o
	 * divisa simulada.
	 */
	private int precio;

	/**
	 * Estado operacional actual de la boletería del encuentro (ej: DISPONIBLE,
	 * AGOTADO).
	 */
	private String estado;

	/**
	 * Zona o grupo de la Copa del Mundo a la que corresponde el partido (Grupos de
	 * la A a la L).
	 */
	private String grupo;

	/**
	 * URL del recurso visual con el escudo o insignia oficial de la selección
	 * local.
	 */
	private String localCrest;

	/**
	 * URL del recurso visual con el escudo o insignia oficial de la selección
	 * visitante.
	 */
	private String visitanteCrest;

	/** @return El ID del encuentro. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de partido a configurar. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return La URL del escudo del equipo local. */
	public String getLocalCrest() {
		return localCrest;
	}

	/** @param localCrest El nuevo enlace del escudo local a configurar. */
	public void setLocalCrest(String localCrest) {
		this.localCrest = localCrest;
	}

	/** @return La URL del escudo del equipo visitante. */
	public String getVisitanteCrest() {
		return visitanteCrest;
	}

	/** @param visitanteCrest El nuevo enlace del escudo visitante a configurar. */
	public void setVisitanteCrest(String visitanteCrest) {
		this.visitanteCrest = visitanteCrest;
	}

	/** @return El nombre del equipo local. */
	public String getLocal() {
		return local;
	}

	/** @param local El nuevo nombre de equipo local a asignar. */
	public void setLocal(String local) {
		this.local = local;
	}

	/** @return El nombre del equipo visitante. */
	public String getVisitante() {
		return visitante;
	}

	/** @param visitante El nuevo nombre de equipo visitante a asignar. */
	public void setVisitante(String visitante) {
		this.visitante = visitante;
	}

	/** @return La programación cronológica del encuentro. */
	public String getFecha() {
		return fecha;
	}

	/** @param fecha La nueva fecha de programación a establecer. */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/** @return El nombre del estadio sede. */
	public String getEstadio() {
		return estadio;
	}

	/** @param estadio El nuevo estadio a asignar. */
	public void setEstadio(String estadio) {
		this.estadio = estadio;
	} 

	/** @return El precio comercial de la entrada. */
	public int getPrecio() {
		return precio;
	}

	/** @param precio El nuevo valor monetario del boleto. */
	public void setPrecio(int precio) {
		this.precio = precio;
	}

	/** @return El estado de disponibilidad de aforo. */
	public String getEstado() {
		return estado;
	}

	/** @param estado El nuevo estado de taquilla a establecer. */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/** @return El grupo del mundial correspondiente. */
	public String getGrupo() {
		return grupo;
	}

	/** @param grupo El nuevo grupo a configurar. */
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
}