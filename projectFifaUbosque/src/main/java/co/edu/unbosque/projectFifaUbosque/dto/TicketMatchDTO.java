package co.edu.unbosque.projectFifaUbosque.dto;

public class TicketMatchDTO {
	private Long id;
	private String local;
	private String visitante;
	private String fecha;
	private String estadio;
	private int precio;
	private String estado;
	private String grupo;
	private String localCrest;
	private String visitanteCrest;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLocalCrest() {
		return localCrest;
	}

	public void setLocalCrest(String localCrest) {
		this.localCrest = localCrest;
	}

	public String getVisitanteCrest() {
		return visitanteCrest;
	}

	public void setVisitanteCrest(String visitanteCrest) {
		this.visitanteCrest = visitanteCrest;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getVisitante() {
		return visitante;
	}

	public void setVisitante(String visitante) {
		this.visitante = visitante;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getEstadio() {
		return estadio;
	}

	public void setEstadio(String estadio) {
		this.estadio = estadio;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

}