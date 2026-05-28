package co.edu.unbosque.projectFifaUbosque.dto;

import java.util.Objects;
import co.edu.unbosque.projectFifaUbosque.model.User.Role;

/**
 * Objeto de Transferencia de Datos (DTO) para la gestión unificada de perfiles
 * de Usuario.
 * <p>
 * Modela los flujos de creación, modificación, ránkings de pollas y transmisión
 * de perfiles en logins y registros del portal, abstrayendo de forma óptima los
 * mapeos directos sobre la entidad del sistema.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class UserDTO {

	/** Llave primaria secuencial del usuario. */
	private Long id;

	/** Alias o nombre único identificador de la cuenta (Username). */
	private String user;

	/** Clave secreta o contraseña de seguridad de acceso. */
	private String password;

	/** Nombre completo y apellidos del usuario. */
	private String name;

	/** Documento legal o número de identificación personal del hincha. */
	private String personalId;

	/** Nombre del país o nacionalidad declarada de origen del usuario. */
	private String coutry;

	/**
	 * URL de la imagen de avatar o foto de perfil del usuario procesada en
	 * Cloudinary.
	 */
	private String avatar;

	/**
	 * Nivel de acceso y permisos del usuario otorgados mediante el rol del sistema
	 * (ADMIN, USER, SUPPORT).
	 */
	private Role role;

	/** Dirección de correo electrónico asociada al perfil. */
	private String email;

	/**
	 * Indicador de recompensa especial por la completación al 100% del álbum
	 * digital (por defecto: false).
	 */
	private boolean albumCompleteReward = false;

	/**
	 * Indicador de verificación de identidad de doble factor vía correo electrónico
	 * completado (por defecto: false).
	 */
	private boolean isVerified = false;

	/**
	 * Código numérico OTP temporal enviado al correo para validación de la cuenta.
	 */
	private int verificationCode;

	/**
	 * Indicador de visualización obligatoria de la ventana de tutorial
	 * introductorio completado por el cliente.
	 */
	private boolean tutorialView;

	/**
	 * Saldo neto de monedas de la aplicación acumuladas por el usuario para compra
	 * de sobres.
	 */
	private int coins;

	/**
	 * Indicador de estado de la cuenta (true si el perfil se encuentra totalmente
	 * activo).
	 */
	private boolean countActive;

	/**
	 * Constructor por defecto para procesos de mapeo relacional y conversiones vía
	 * JSON.
	 */
	public UserDTO() {
	}

	/**
	 * Constructor full de campos para inicializaciones robustas de datos del
	 * usuario en la plataforma.
	 *
	 * @param user                Alias de cuenta.
	 * @param password            Contraseña cifrada o en claro.
	 * @param name                Nombre completo.
	 * @param personalId          Cédula o documento de identidad.
	 * @param coutry              País origen.
	 * @param avatar              Enlace del recurso multimedia.
	 * @param email               Correo electrónico.
	 * @param albumCompleteReward Estado de la recompensa del álbum.
	 * @param verificationCode    Código OTP asignado.
	 * @param isVerified          Estado de verificación.
	 * @param tutorialView        Visualización del tutorial.
	 * @param countActive         Estado de la cuenta.
	 */
	public UserDTO(String user, String password, String name, String personalId, String coutry, String avatar,
			String email, boolean albumCompleteReward, int verificationCode, boolean isVerified, boolean tutorialView,
			boolean countActive) {
		this.user = user;
		this.password = password;
		this.name = name;
		this.personalId = personalId;
		this.coutry = coutry;
		this.avatar = avatar;
		this.albumCompleteReward = albumCompleteReward;
		this.verificationCode = verificationCode;
		this.isVerified = isVerified;
		this.tutorialView = tutorialView;
		this.countActive = countActive;
	}

	/** @return ID numérico del usuario. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID numérico a asignar. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El balance actual de monedas acumuladas. */
	public int getCoins() {
		return coins;
	}

	/** @param coins El nuevo monto de monedas a establecer en el balance. */
	public void setCoins(int coins) {
		this.coins = coins;
	}

	/** @return true si la cuenta está completamente activa. */
	public boolean isCountActive() {
		return countActive;
	}

	/** @param countActive El nuevo estado operacional de la cuenta. */
	public void setCountActive(boolean countActive) {
		this.countActive = countActive;
	}

	/** @return true si el usuario ya vio la ventana de tutorial. */
	public boolean isTutorialView() {
		return tutorialView;
	}

	/** @param tutorialView El nuevo estado de visualización de tutorial. */
	public void setTutorialView(boolean tutorialView) {
		this.tutorialView = tutorialView;
	}

	/** @return true si el perfil está verificado en doble factor. */
	public boolean isVerified() {
		return isVerified;
	}

	/** @param isVerified El nuevo estado de verificación a configurar. */
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	/** @return Código OTP actual de validación de correo. */
	public int getVerificationCode() {
		return verificationCode;
	}

	/** @param verificationCode El nuevo código numérico de verificación. */
	public void setVerificationCode(int verificationCode) {
		this.verificationCode = verificationCode;
	}

	/** @return El alias de usuario. */
	public String getUser() {
		return user;
	}

	/** @param user El nuevo alias de usuario a asignar. */
	public void setUser(String user) {
		this.user = user;
	}

	/** @return Contraseña de la cuenta. */
	public String getPassword() {
		return password;
	}

	/** @param password La nueva contraseña de acceso. */
	public void setPassword(String password) {
		this.password = password;
	}

	/** @return El nombre completo. */
	public String getName() {
		return name;
	}

	/** @param name El nuevo nombre completo del usuario. */
	public void setName(String name) {
		this.name = name;
	}

	/** @return Documento de identidad personal. */
	public String getPersonalId() {
		return personalId;
	}

	/** @param personalId El nuevo documento de identidad personal. */
	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	/** @return true si el usuario ya cobró el premio por completar el álbum. */
	public boolean isAlbumCompleteReward() {
		return albumCompleteReward;
	}

	/** @param albumCompleteReward El nuevo estado de la recompensa especial. */
	public void setAlbumCompleteReward(boolean albumCompleteReward) {
		this.albumCompleteReward = albumCompleteReward;
	}

	/** @return País de procedencia configurado. */
	public String getCoutry() {
		return coutry;
	}

	/** @param coutry El nuevo país de procedencia a asignar. */
	public void setCoutry(String coutry) {
		this.coutry = coutry;
	}

	/** @return El rol operacional asignado al perfil. */
	public Role getRole() {
		return role;
	}

	/** @param role El nuevo rol de seguridad a configurar. */
	public void setRole(Role role) {
		this.role = role;
	}

	/** @return Enlace web de la foto de perfil. */
	public String getAvatar() {
		return avatar;
	}

	/** @param avatar La nueva URL de la imagen del avatar. */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/** @return La dirección de correo electrónico vinculada. */
	public String getEmail() {
		return email;
	}

	/** @param email La nueva dirección de correo electrónico. */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Computa el código hash único del DTO de usuario considerando campos de
	 * seguridad críticos.
	 *
	 * @return Código hash generado.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, password, role, user);
	}

	/**
	 * Evalúa la igualdad estructural de dos objetos UserDTO basándose en id,
	 * password, rol y username.
	 *
	 * @param obj Objeto a comparar.
	 * @return true si ambos DTOs comparten los mismos atributos lógicos de
	 *         identidad.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password) && role == other.role
				&& Objects.equals(user, other.user);
	}

	/**
	 * Genera un reporte en formato texto con los datos básicos del perfil de
	 * usuario, protegiendo credenciales sensibles.
	 *
	 * @return Cadena descriptiva del UserDTO.
	 */
	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", user=" + user + ", password=" + password + ", name=" + name + ", personalId="
				+ personalId + ", coutry=" + coutry + ", role=" + role + ", email=" + email + "]";
	}
}