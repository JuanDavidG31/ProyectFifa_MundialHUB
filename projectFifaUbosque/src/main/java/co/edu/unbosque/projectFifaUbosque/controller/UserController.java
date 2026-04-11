package co.edu.unbosque.projectFifaUbosque.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.projectFifaUbosque.dto.UserDTO;
import co.edu.unbosque.projectFifaUbosque.service.UserService;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.swagger.v3.oas.annotations.media.Content;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.Transformation;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@Transactional
@Tag(name = "User Management", description = "Endpoints for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
	@Autowired
	private UserService userServ;

	@Autowired
	private Cloudinary cloudinary;

	public UserController() {
	}

	@PutMapping(value = "/actualizar-foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> actualizarFotoPerfil(@RequestParam long id,
			@Parameter(description = "Nueva foto de perfil", required = true, name = "archivo", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) @RequestParam("archivo") MultipartFile archivo) {

		if (archivo.isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("message", "Por favor, selecciona una foto de perfil para subir.", "success", false));
		}

		try {

			Map uploadResult = cloudinary.uploader().upload(archivo.getBytes(),
					ObjectUtils.asMap("transformation", new Transformation().width(250).height(250).crop("fill")
							.gravity("face").radius("max").fetchFormat("auto").quality("auto")));

			String avatarUrl = (String) uploadResult.get("secure_url");

			UserDTO userDTO = new UserDTO();
			userDTO.setAvatar(avatarUrl);
			int resultado = userServ.updateImage(id, userDTO);

			if (resultado == 0) {
				return ResponseEntity.ok(Map.of("avatar", avatarUrl,

						"message", "Avatar generado y guardado exitosamente.", "success", true));
			} else if (resultado == 2) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("message", "No se encontró el usuario con ID: " + id, "success", false));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
						Map.of("message", "Error al actualizar la foto de perfil del usuario.", "success", false));
			}

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message",
					"Error al procesar y subir la foto de perfil: " + e.getMessage(), "success", false));
		}
	}

	@GetMapping("/showAll")
	public ResponseEntity<List<UserDTO>> showAllEncrypted() {
		List<UserDTO> users = userServ.getAll();

		if (users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
		}
	}

	@PutMapping(path = "/updateStatusView")
	ResponseEntity<Map<String, Boolean>> updateStatusView(@RequestParam long id) {

		int resultado = userServ.updateStatusTutorial(id);

		if (resultado == 0) {
			return ResponseEntity.ok(Map.of("success", true));
		} else if (resultado == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
		}

	}

	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(@RequestParam Long id, @RequestBody UserDTO newUser) {

		int status = userServ.updateById(id, newUser);

		if (status == 0) {
			return new ResponseEntity<>("User updated successfully", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("New username already taken", HttpStatus.IM_USED);
		} else if (status == 2) {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Error on update", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = userServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(@PathVariable Long id) {
		boolean found = userServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/getbyid/{id}")
	ResponseEntity<UserDTO> getById(@PathVariable Long id) {
		UserDTO found = userServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/getbyuser/{user}")
	public ResponseEntity<UserDTO> getByUser(@PathVariable String user) {
		UserDTO found = userServ.getByUser(user);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/eliminarId/{id}")

	public ResponseEntity<String> deleteById(@PathVariable Long id) {

		int status = userServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado con exito", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error al eliminar el usuario", HttpStatus.NOT_FOUND);
		}
	}

}