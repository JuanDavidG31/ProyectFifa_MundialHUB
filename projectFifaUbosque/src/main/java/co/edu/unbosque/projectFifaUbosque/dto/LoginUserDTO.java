package co.edu.unbosque.projectFifaUbosque.dto;

import java.util.Objects;

/**
 * Objeto de Transferencia de Datos (DTO) para procesar las credenciales de
 * autenticación en inicios de sesión (Login).
 * <p>
 * Transporta el nombre de usuario y la contraseña en texto claro enviados desde
 * los formularios frontend hacia los filtros de seguridad del backend.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class LoginUserDTO {

	/** Nombre de usuario o alias ingresado para el login. */
	private String user;

	/** Contraseña de seguridad de la cuenta. */
	private String password;

	/**
	 * Constructor por defecto para los procesos de mapeo y serialización.
	 */
	public LoginUserDTO() {
		// Constructor base vacío.
	}

	/**
	 * Constructor parametrizado para instanciación directa de credenciales de
	 * autenticación.
	 *
	 * @param user     Nombre de usuario.
	 * @param password Contraseña de acceso.
	 */
	public LoginUserDTO(String user, String password) {
		this.user = user;
		this.password = password;
	}

	/** @return El alias o nombre de usuario. */
	public String getUser() {
		return user;
	}

	/** @param user El nuevo nombre de usuario a establecer. */
	public void setUser(String user) {
		this.user = user;
	}

	/** @return La contraseña en texto plano. */
	public String getPassword() {
		return password;
	}

	/** @param password La nueva contraseña a establecer. */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Genera el valor Hash del objeto basándose en las propiedades de seguridad de
	 * contraseña y alias.
	 *
	 * @return Código numérico hash computed.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(password, user);
	}

	/**
	 * Retorna la representación del objeto de login, omitiendo por seguridad la
	 * contraseña en texto claro.
	 *
	 * @return Cadena descriptiva de LoginUserDTO.
	 */
	@Override
	public String toString() {
		return "LoginUserDTO [user=" + user + ", password=" + password + "]";
	}
}