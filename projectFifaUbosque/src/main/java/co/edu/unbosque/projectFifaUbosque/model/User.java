package co.edu.unbosque.projectFifaUbosque.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad de persistencia principal que representa las cuentas de usuario de la
 * plataforma.
 * <p>
 * Implementa {@link UserDetails} de Spring Security para integrarse de forma
 * nativa con los mecanismos de autenticación, control de accesos basados en
 * roles y validación de tokens.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Entity
@Table(name = "useraccount")
public class User implements UserDetails {

	/** Identificador único serial para la serialización de la clase. */
	private static final long serialVersionUID = 1L;

	/** Llave primaria autoincremental del usuario en la base de datos. */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	/**
	 * Alias o nombre único identificador de la cuenta (encriptado mediante AES).
	 */
	@Column(unique = true, name = "username")
	private String user;

	/** Contraseña cifrada del usuario utilizando hash BCrypt. */
	private String password;

	/** Nombre completo del usuario. */
	private String name;

	/** Número de identificación personal o documento legal (único). */
	@Column(unique = true)
	private String personalId;

	/** País o nacionalidad de procedencia del usuario. */
	private String coutry;

	/** URL absoluta de la foto de perfil almacenada en Cloudinary. */
	private String avatar;

	/** Dirección de correo electrónico asociada al perfil (única). */
	@Column(unique = true)
	private String email;

	/** Edad del usuario en años. */
	private int age;

	/** Indicador de si el usuario ha reclamado el premio por completar el álbum. */
	private boolean albumCompleteReward = false;

	/** Indicador de si el usuario ya vio el tutorial inicial del sistema. */
	private boolean tutorialView = false;

	/** Estado de verificación en dos pasos (OTP) vía correo. */
	@Column(name = "is_verified")
	private boolean isVerified = false;

	/** Código numérico temporal OTP enviado para la activación de la cuenta. */
	@Column(name = "verificationCode")
	private int verificationCode = 0;

	/** Estado de actividad de la cuenta del usuario. */
	private boolean countActive = false;

	/** Rol y nivel de permisos asignado al usuario mediante enumerador. */
	@Enumerated(EnumType.STRING)
	private Role role;

	/** Estado de expiración de la cuenta. */
	private boolean accountNonExpired;

	/** Estado de bloqueo administrativo de la cuenta. */
	private boolean accountNonLocked;

	/** Estado de expiración de las credenciales de acceso. */
	private boolean credentialsNonExpired;

	/** Indicador de si la cuenta se encuentra habilitada operativamente. */
	private boolean enabled;

	/**
	 * Cantidad de sobres virtuales disponibles en el inventario del usuario para
	 * ser abiertos.
	 */
	@Column(name = "available_packs")
	private int availablePacks = 5;

	/** Cantidad de monedas acumuladas en la billetera virtual de la aplicación. */
	@Column(name = "coins")
	private int coins = 0;

	/**
	 * Constructor por defecto encargado de configurar los valores iniciales y
	 * banderas de seguridad.
	 */
	public User() {
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.role = null;
	}

	/**
	 * Constructor parametrizado para inicializaciones completas de cuentas.
	 *
	 * @param user                Alias único.
	 * @param password            Contraseña cifrada.
	 * @param name                Nombre completo.
	 * @param personalId          Documento legal.
	 * @param coutry              País.
	 * @param avatar              Ruta de imagen de perfil.
	 * @param email               Correo electrónico.
	 * @param albumCompleteReward Recompensa del álbum.
	 * @param verificationCode    Código OTP.
	 * @param isVerified          Estado de verificación.
	 * @param tutorialView        Visualización del tutorial.
	 * @param countActive         Cuenta activa.
	 */
	public User(String user, String password, String name, String personalId, String coutry, String avatar,
			String email, boolean albumCompleteReward, int verificationCode, boolean isVerified, boolean tutorialView,
			boolean countActive) {
		this();
		this.user = user;
		this.password = password;
		this.name = name;
		this.personalId = personalId;
		this.coutry = coutry;
		this.avatar = avatar;
		this.email = email;
		this.albumCompleteReward = albumCompleteReward;
		this.verificationCode = verificationCode;
		this.isVerified = isVerified;
		this.tutorialView = tutorialView;
		this.countActive = countActive;
	}

	/**
	 * Enumerador con los roles admitidos dentro del control de acceso de la
	 * aplicación.
	 */
	public enum Role {
		/** Rol estándar para los jugadores y coleccionistas. */
		USER,
		/** Rol para la gestión administrativa del catálogo de láminas y usuarios. */
		ADMIN,
		/**
		 * Rol asignado a los agentes encargados de atender el chat de soporte técnico.
		 */
		SUPPORT
	}

	/** @return El enlace multimedia del avatar. */
	public String getAvatar() {
		return avatar;
	}

	/** @param avatar Nueva URL del avatar. */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/** @return true si la cuenta está activa. */
	public boolean isCountActive() {
		return countActive;
	}

	/** @param countActive El nuevo estado de actividad. */
	public void setCountActive(boolean countActive) {
		this.countActive = countActive;
	}

	/** @return El ID de base de datos. */
	public Long getId() {
		return id;
	}

	/** @param id El nuevo ID de persistencia. */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return El alias cifrado del usuario. */
	public String getUser() {
		return user;
	}

	/** @param user El nuevo alias a establecer. */
	public void setUser(String user) {
		this.user = user;
	}

	/** @return true si el perfil está verificado. */
	public boolean isVerified() {
		return isVerified;
	}

	/** @param isVerified El nuevo estado de verificación. */
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	/** @return El código OTP actual. */
	public int getVerificationCode() {
		return verificationCode;
	}

	/** @param verificationCode El nuevo código OTP. */
	public void setVerificationCode(int verificationCode) {
		this.verificationCode = verificationCode;
	}

	/** @return La contraseña cifrada. */
	public String getPassword() {
		return password;
	}

	/** @param password La nueva contraseña. */
	public void setPassword(String password) {
		this.password = password;
	}

	/** @return El nombre completo. */
	public String getName() {
		return name;
	}

	/** @param name El nuevo nombre completo. */
	public void setName(String name) {
		this.name = name;
	}

	/** @return El saldo de monedas virtuales. */
	public int getCoins() {
		return coins;
	}

	/** @param coins El nuevo saldo de monedas. */
	public void setCoins(int coins) {
		this.coins = coins;
	}

	/** @return El documento de identificación. */
	public String getPersonalId() {
		return personalId;
	}

	/** @param personalId El nuevo documento legal. */
	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	/** @return El país de origen configurado. */
	public String getCoutry() {
		return coutry;
	}

	/** @param coutry El nuevo país a asignar. */
	public void setCoutry(String coutry) {
		this.coutry = coutry;
	}

	/** @return El rol administrativo o de usuario. */
	public Role getRole() {
		return role;
	}

	/** @param role El nuevo rol a configurar. */
	public void setRole(Role role) {
		this.role = role;
	}

	/** @return Cantidad de sobres de láminas disponibles. */
	public int getAvailablePacks() {
		return availablePacks;
	}

	/** @param availablePacks La nueva cantidad de sobres. */
	public void setAvailablePacks(int availablePacks) {
		this.availablePacks = availablePacks;
	}

	/** @return true si la recompensa ya fue adjudicada. */
	public boolean isAlbumCompleteReward() {
		return albumCompleteReward;
	}

	/** @param albumCompleteReward El nuevo estado de la recompensa. */
	public void setAlbumCompleteReward(boolean albumCompleteReward) {
		this.albumCompleteReward = albumCompleteReward;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, password, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password)
				&& Objects.equals(user, other.user);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getUsername() {
		return user;
	}

	/** @return El correo electrónico del usuario. */
	public String getEmail() {
		return email;
	}

	/** @param email El nuevo correo a asignar. */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @return La edad en años. */
	public int getAge() {
		return age;
	}

	/** @param age La nueva edad. */
	public void setAge(int age) {
		this.age = age;
	}

	/** @return true si ya visualizó el tutorial. */
	public boolean isTutorialView() {
		return tutorialView;
	}

	/** @param tutorialView El nuevo estado de visualización del tutorial. */
	public void setTutorialView(boolean tutorialView) {
		this.tutorialView = tutorialView;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", user=" + user + ", password=" + password + ", name=" + name + ", personalId="
				+ personalId + ", coutry=" + coutry + ", email=" + email + ", age=" + age + "]";
	}
}