package co.edu.unbosque.projectFifaUbosque.dto;

/**
 * Objeto de Transferencia de Datos (DTO) para la cotización de itinerarios de
 * vuelos comerciales.
 * <p>
 * Transporta la información de transporte aéreo asociada a los paquetes de
 * viaje oficiales hacia las sedes del torneo de la Copa del Mundo.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class FlightDTO {

	/** Nombre comercial de la aerolínea encargada de operar el vuelo. */
	private String airline;

	/** URL del logotipo de la compañía de aviación. */
	private String airlineLogo;

	/** Fecha e indicador horario de salida o despegue. */
	private String departureTime;

	/** Fecha e indicador horario estimado de aterrizaje o llegada. */
	private String arrivalTime;

	/** Duración neta del viaje expresada en minutos. */
	private int durationMinutes;

	/** Tarifa o precio comercial del boleto de avión. */
	private int price;

	/**
	 * Constructor base por defecto.
	 */
	public FlightDTO() {
		// Constructor vacío serializable.
	}

	/** @return El nombre de la aerolínea. */
	public String getAirline() {
		return airline;
	}

	/** @param airline El nuevo nombre de aerolínea a establecer. */
	public void setAirline(String airline) {
		this.airline = airline;
	}

	/** @return La URL del logotipo de la aerolínea. */
	public String getAirlineLogo() {
		return airlineLogo;
	}

	/** @param airlineLogo La nueva URL del logo a establecer. */
	public void setAirlineLogo(String airlineLogo) {
		this.airlineLogo = airlineLogo;
	}

	/** @return El horario de salida. */
	public String getDepartureTime() {
		return departureTime;
	}

	/** @param departureTime El nuevo horario de salida a establecer. */
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	/** @return El horario de llegada. */
	public String getArrivalTime() {
		return arrivalTime;
	}

	/** @param arrivalTime El nuevo horario de llegada a establecer. */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/** @return La duración del viaje en minutos. */
	public int getDurationMinutes() {
		return durationMinutes;
	}

	/** @param durationMinutes Los minutos de duración a establecer. */
	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	/** @return El precio comercial del vuelo. */
	public int getPrice() {
		return price;
	}

	/** @param price El nuevo precio de tarifa a establecer. */
	public void setPrice(int price) {
		this.price = price;
	}
}