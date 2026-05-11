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

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "*" })
@Tag(name = "Autenticación", description = "API para autenticación de usuarios (login y registro)")
public class AuthController {

	private final FootballController footballController;

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserService userService;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ExternalHTTPRequestHandler externalHttpHandler;

	@Autowired
	private EmailService emailService;

	@Autowired
	private Cloudinary cloudinary;

	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService,
			FootballController footballController) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
		this.footballController = footballController;
	}

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

	@GetMapping("/codigo")
	public ResponseEntity<?> codigo(@RequestParam String user) {
		UserDTO userDTO = userService.getByUser(user);

		if (userDTO != null) {
			return ResponseEntity.ok().body(userDTO.getVerificationCode());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	}

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

	private static class AuthResponse {
		private final String token;
		private final String role;
		private final String avatar;
		private final boolean albumCompleteReward;
		private final boolean verify;
		private final boolean tutorialView;
		private final boolean countActive;

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