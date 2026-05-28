package co.edu.unbosque.projectFifaUbosque.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import co.edu.unbosque.projectFifaUbosque.dto.LoginUserDTO;
import co.edu.unbosque.projectFifaUbosque.dto.UserDTO;
import co.edu.unbosque.projectFifaUbosque.model.User;
import co.edu.unbosque.projectFifaUbosque.repository.UserRepository;
import co.edu.unbosque.projectFifaUbosque.security.JwtUtil;
import co.edu.unbosque.projectFifaUbosque.service.EmailService;
import co.edu.unbosque.projectFifaUbosque.service.ExternalHTTPRequestHandler;
import co.edu.unbosque.projectFifaUbosque.service.UserService;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador de Seguridad encargado de los procesos lógicos de Autenticación y
 * Registro.
 * <p>
 * Ofrece soporte para inicio de sesión seguro (generación de tokens JWT),
 * registro multimedial de perfiles con carga asíncrona de avatares a
 * Cloudinary, restablecimiento y envío de códigos OTP mediante servicios de
 * email.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "*" })
@Tag(name = "Autenticación", description = "API para autenticación de usuarios (login y registro)")
public class AuthController {
	/** Instancia del controlador de fútbol relacionado. */
	private final FootballController footballController;
	/** Gestor principal de autenticación provisto por Spring Security. */
	private final AuthenticationManager authenticationManager;
	/** Utilidad criptográfica para tokens estructurados. */
	private final JwtUtil jwtUtil;
	/** Servicio de control y reglas del negocio de usuarios. */
	private final UserService userService;
	/** Acceso a la persistencia de usuarios. */
	@Autowired
	private UserRepository userRepository;
	/** Codificador Hash bcrypt para llaves secretas. */
	@Autowired
	private PasswordEncoder passwordEncoder;
	/** Manejador HTTP externo de peticiones de soporte. */
	@Autowired
	private ExternalHTTPRequestHandler externalHttpHandler;
	/** Servicio de envío de notificaciones y correos. */
	@Autowired
	private EmailService emailService;
	/** Servicio de almacenamiento en la nube Cloudinary. */
	@Autowired
	private Cloudinary cloudinary;

	/**
	 * Constructor único con inyección de dependencias obligatorias.
	 *
	 * @param authenticationManager Gestor de logins.
	 * @param jwtUtil               Utilidad Jwt.
	 * @param userService           Manejo de usuarios.
	 * @param footballController    Controlador deportivo asociado.
	 */
	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService,
			FootballController footballController) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
		this.footballController = footballController;
	}

	/**
	 * Actualiza o restablece la contraseña de acceso de un usuario previa
	 * validación de su email cifrado.
	 *
	 * @param email       Correo del usuario que solicita el cambio.
	 * @param newPassword Nueva contraseña en texto plano para encriptar mediante
	 *                    hash.
	 * @return {@link ResponseEntity} indicando el éxito de la operación o error
	 *         404.
	 */

	@PutMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {

		String encryptedEmail = AESUtil.encrypt(email);

		Optional<User> userOpt = userRepository.findByEmail(encryptedEmail);

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			return ResponseEntity.ok().body("Contraseña actualizada correctamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no encontrado");
		}
	}

	/**
	 * Redirecciona mediante HTTP 302 hacia la URL multimedia almacenada de un
	 * archivo dentro de Cloudinary.
	 *
	 * @param urlCloudinary Dirección absoluta de la imagen.
	 * @return Redirección fluida a la ubicación de recursos.
	 */
	@GetMapping("/archivo/ver")
	public ResponseEntity<?> obtenerArchivo(@RequestParam(value = "url", required = false) String urlCloudinary) {
		try {
			String finalUrl = (urlCloudinary != null && urlCloudinary.startsWith("http")) ? urlCloudinary
					: "https://tu-url-de-imagen-por-defecto.png";

			return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(finalUrl)).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	/**
	 * Retorna el código numérico OTP temporal asignado actualmente a un usuario
	 * específico.
	 *
	 * @param user Nombre de usuario a consultar.
	 * @return {@link ResponseEntity} con el valor del código o estado de error no
	 *         encontrado.
	 */
	@GetMapping("/codigo")
	public ResponseEntity<?> codigo(@RequestParam String user) {
		UserDTO userDTO = userService.getByUser(user);

		if (userDTO != null) {
			return ResponseEntity.ok().body(userDTO.getVerificationCode());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	}

	/**
	 * Genera un cuerpo HTML estructurado y despacha un correo electrónico con el
	 * código OTP de seguridad para la cuenta.
	 *
	 * @param user Nombre del usuario.
	 * @return Estado HTTP indicando si el mensaje fue encolado/despachado
	 *         satisfactoriamente.
	 */
	@PostMapping("/sendEmailVerifyCode")
	public ResponseEntity<?> sendEmailVerifyCode(@RequestParam String user) {
		UserDTO userDTO = userService.getByUser(user);

		if (userDTO != null) {
			String email = userDTO.getEmail();
			String asunto = "🔒 Bienvenido a MundialHub - Código de Verificación";

			String htmlBody = "<div style='font-family: Arial, sans-serif; background-color: #0b0f19; color: #ffffff; padding: 30px; text-align: center; border-radius: 15px; max-width: 500px; margin: 0 auto;'>"
					+ "<h2 style='color: #ec4899; margin-bottom: 5px;'>¡Verifica tu cuenta!</h2>"
					+ "<p style='color: #94a3b8; font-size: 16px;'>Usa el siguiente código de seguridad para acceder a tu cuenta en MundialHub:</p>"
					+ "<div style='background-color: rgba(255,255,255,0.05); padding: 20px; border-radius: 10px; margin: 25px auto; max-width: 200px; border: 1px solid rgba(255,255,255,0.1); font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #06b6d4;'>"
					+ userDTO.getVerificationCode() + "</div>"
					+ "<p style='margin-top: 30px; font-size: 12px; color: #64748b;'>Si no solicitaste este código, por favor ignora este correo. Tu cuenta está segura.</p>"
					+ "</div>";

			if (emailService.sendEmail(email, asunto, htmlBody)) {
				return ResponseEntity.ok(new AuthResponse(null, null, null, false, false, false, false));
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar correo");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	}

	/**
	 * Valida la identidad y marca el estado del perfil como verificado ('verified'
	 * = true) eliminando códigos temporales.
	 *
	 * @param user Nombre del usuario.
	 * @return {@link ResponseEntity} con la confirmación de la verificación.
	 */
	@PutMapping("/updateVerify")
	public ResponseEntity<?> updateVerify(@RequestParam String user) {
		Optional<User> userOpt = userRepository.findByUser(AESUtil.encrypt(user));

		if (userOpt.isPresent()) {
			User u = userOpt.get();
			u.setVerificationCode(0);
			u.setVerified(true);
			userRepository.save(u);
			return ResponseEntity.ok().body("Estado de verificación actualizado correctamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}
	}

	/**
	 * Endpoint principal de Login. Valida las credenciales contra el gestor de
	 * autenticación de Spring y produce el JSON de respuesta con el token JWT de
	 * sesión e indicadores de perfil.
	 *
	 * @param loginRequest DTO contenedor de las credenciales brutas (user y
	 *                     password).
	 * @return {@link ResponseEntity} con un {@link AuthResponse} cargado o Http 401
	 *         si falla.
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginUserDTO loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					userService.encryptLogin(loginRequest).getUser(), loginRequest.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateToken(userDetails, AESUtil.encrypt(loginRequest.getUser()));

			String role = (userDetails instanceof User) ? ((User) userDetails).getRole().name() : null;

			UserDTO loggedInUser = userService.getByUser(loginRequest.getUser());
			String avatar = (loggedInUser != null) ? loggedInUser.getAvatar() : null;
			boolean verify = (loggedInUser != null) ? loggedInUser.isVerified() : null;
			boolean albumCompleteReward = (loggedInUser != null) ? loggedInUser.isAlbumCompleteReward() : null;
			boolean tutorialView = (loggedInUser != null) ? loggedInUser.isTutorialView() : null;
			boolean countActive = (loggedInUser != null) ? loggedInUser.isCountActive() : null;
			return ResponseEntity
					.ok(new AuthResponse(jwt, role, avatar, albumCompleteReward, verify, tutorialView, countActive));

		} catch (AuthenticationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Nombre de usuario o contraseña inválidos o usuario no encontrado");
		}
	}

	/**
	 * Registra un nuevo usuario en la base de datos de manera multipart,
	 * permitiendo la carga segura e inmediata de una imagen de avatar personal a
	 * Cloudinary aplicando recortes automáticos basados en reconocimiento facial.
	 *
	 * @param registerRequest DTO estructurado con los datos del usuario.
	 * @param rol             Identificador de rol asignado por URL.
	 * @param archivo         Archivo binario opcional (Multipart) de la foto de
	 *                        perfil del usuario.
	 * @return {@link ResponseEntity} con la información pública del usuario
	 *         registrado, o 409 Conflict.
	 */
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> register(@RequestPart("data") UserDTO registerRequest, @RequestParam String rol,
			@Parameter(description = "Foto de perfil (opcional)", name = "archivo", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) @RequestPart(value = "archivo", required = false) MultipartFile archivo) {

		if (userService.findUsernameAlreadyTaken(registerRequest.getUser())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "El nombre de usuario ya existe"));
		}

		int result = userService.create(registerRequest, rol);

		if (result != 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al registrar usuario", "success", false));
		}

		UserDTO userCreado = userService.getByUser(registerRequest.getUser());

		if (archivo != null && !archivo.isEmpty() && userCreado != null) {
			try {
				@SuppressWarnings("rawtypes")
				Map uploadResult = cloudinary.uploader().upload(archivo.getBytes(),
						ObjectUtils.asMap("transformation", new Transformation().width(250).height(250).crop("fill")
								.gravity("face").radius("max").fetchFormat("auto").quality("auto")));

				String avatarUrl = (String) uploadResult.get("secure_url");

				UserDTO avatarDTO = new UserDTO();
				avatarDTO.setAvatar(avatarUrl);
				userService.updateImage(userCreado.getId(), avatarDTO);

				userCreado.setAvatar(avatarUrl);

			} catch (IOException e) {

			}
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(userCreado);
	}

	/**
	 * Clase interna POJO estática diseñada para formatear la estructura JSON de
	 * respuesta en logins exitosos.
	 */
	private static class AuthResponse {
		private final String token;
		private final String role;
		private final String avatar;
		private final boolean albumCompleteReward;
		private final boolean verify;
		private final boolean tutorialView;
		private final boolean countActive;

		/**
		 * Constructor full de campos de respuesta de autenticación.
		 *
		 * @param token               Token JWT firmado.
		 * @param role                Nombre del rol.
		 * @param avatar              URL pública de la imagen de avatar.
		 * @param albumCompleteReward Recompensa del álbum.
		 * @param verify              Estado verificado.
		 * @param tutorialView        Visualización del tutorial.
		 * @param countActive         Cuenta activa.
		 */
		public AuthResponse(String token, String role, String avatar, boolean albumCompleteReward, boolean verify,
				boolean tutorialView, boolean countActive) {
			this.token = token;
			this.role = role;
			this.avatar = avatar;
			this.albumCompleteReward = albumCompleteReward;
			this.verify = verify;
			this.tutorialView = tutorialView;
			this.countActive = countActive;
		}

		public String getToken() {
			return token;
		}

		public String getRole() {
			return role;
		}

		public boolean isCountActive() {
			return countActive;
		}

		public String getAvatar() {
			return avatar;
		}

		public boolean isAlbumCompleteReward() {
			return albumCompleteReward;
		}

		public boolean isVerify() {
			return verify;
		}

		public boolean isTutorialView() {
			return tutorialView;
		}

	}
}