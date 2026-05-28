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

/**
 * Controlador REST maestro encargado de la administración integral de perfiles
 * de usuario (User Management).
 * <p>
 * Provee servicios protegidos por token Bearer para actualización de avatares
 * vía Cloudinary, recarga de monedas, modificación de estados de conexión de
 * soporte, mutaciones de perfil mediante JSON y consultas operacionales
 * básicas.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@Transactional
@Tag(name = "User Management", description = "Endpoints for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
	/**
	 * Servicio maestro contenedor de las reglas lógicas y cifrados de datos de
	 * usuario.
	 */
	@Autowired
	private UserService userServ;
	/** Inyección del servicio de almacenamiento multimedia oficial. */
	@Autowired
	private Cloudinary cloudinary;

	/**
	 * Constructor por defecto.
	 */
	public UserController() {
	}

	/**
	 * Incrementa de forma directa el balance de monedas virtuales internas (Coins)
	 * asociadas a un usuario.
	 *
	 * @param username Identificador textual de la cuenta en texto claro.
	 * @param amount   Monto total de monedas a acreditar.
	 * @return {@link ResponseEntity} con el nuevo balance acumulado de monedas del
	 *         usuario.
	 */
	@PutMapping("/recharge")
	public ResponseEntity<Integer> rechargeCoins(@RequestParam String username, @RequestParam int amount) {
		int newBalance = userServ.rechargeCoins(username, amount);
		return new ResponseEntity<>(newBalance, HttpStatus.OK);
	}

	/**
	 * Verifica si el ecosistema actual cuenta con algún agente de soporte técnico
	 * en estado activo y disponible.
	 *
	 * @return {@link ResponseEntity} envolviendo un booleano informativo (true si
	 *         hay soporte activo).
	 */
	@GetMapping("/active-support")
	public ResponseEntity<Boolean> checkActiveSupport() {
		return ResponseEntity.ok(userServ.hasActiveSupport());
	}

	/**
	 * Actualiza de forma atómica la foto de perfil (avatar) de un usuario mediante
	 * Multipart e interactúa con Cloudinary.
	 *
	 * @param id      Llave primaria secuencial del usuario.
	 * @param archivo Archivo de imagen binario transferido desde el cliente.
	 * @return {@link ResponseEntity} indicando el éxito y la nueva URL generada, o
	 *         estados 404/500 si falla.
	 */
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

	/**
	 * Recupera la lista completa de todos los usuarios registrados en el sistema
	 * empaquetados en objetos DTO.
	 *
	 * @return {@link ResponseEntity} conteniendo el listado completo de usuarios o
	 *         código 204 si está vacía.
	 */
	@GetMapping("/showAll")
	public ResponseEntity<List<UserDTO>> showAllEncrypted() {
		List<UserDTO> users = userServ.getAll();

		if (users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Actualiza el estado del indicador de vista del tutorial inicial del usuario.
	 *
	 * @param id Llave primaria del usuario.
	 * @return {@link ResponseEntity} indicando el resultado del cambio de estado.
	 */
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

	/**
	 * Actualiza el estado de conexión de un usuario o agente a falso
	 * (Desconectado).
	 *
	 * @param id Identificador numérico del usuario.
	 * @return Mapa indicando el éxito o fracaso de la solicitud.
	 */
	@PutMapping(path = "/updateStatusConnectFalse")
	ResponseEntity<Map<String, Boolean>> updateStatusConnectFalse(@RequestParam long id) {

		int resultado = userServ.updateStatusConnectFalse(id);

		if (resultado == 0) {
			return ResponseEntity.ok(Map.of("success", true));
		} else if (resultado == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
		}

	}

	/**
	 * Actualiza el estado de conexión de un usuario o agente a verdadero (Conectado
	 * / Disponible).
	 *
	 * @param id Identificador numérico del usuario.
	 * @return Mapa indicando el éxito o fracaso de la solicitud.
	 */
	@PutMapping(path = "/updateStatusConnectTrue")
	ResponseEntity<Map<String, Boolean>> updateStatusConnectTrue(@RequestParam long id) {

		int resultado = userServ.updateStatusConnectTrue(id);

		if (resultado == 0) {
			return ResponseEntity.ok(Map.of("success", true));
		} else if (resultado == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
		}

	}

	/**
	 * Actualiza de forma integral la información de un perfil de usuario utilizando
	 * un cuerpo estructurado JSON.
	 *
	 * @param id      Identificador del usuario a modificar.
	 * @param newUser Objeto DTO con los nuevos valores a sobreescribir.
	 * @return {@link ResponseEntity} con cadenas descriptivas de los estados HTTP
	 *         correspondientes (202, 404, 406).
	 */
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

	/**
	 * Consulta la cantidad total de cuentas de usuarios registrados en la base de
	 * datos de la plataforma.
	 *
	 * @return {@link ResponseEntity} conteniendo el recuento total cuantitativo.
	 */
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = userServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Verifica la existencia de una cuenta de usuario mapeada por su identificador
	 * único ID.
	 *
	 * @param id Identificador numérico del usuario.
	 * @return {@link ResponseEntity} con valor booleano true si existe en los
	 *         registros.
	 */
	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(@PathVariable Long id) {
		boolean found = userServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * Recupera la información de un usuario formateada en DTO a partir de su ID
	 * secuencial numérico.
	 *
	 * @param id Identificador único numérico del usuario.
	 * @return {@link ResponseEntity} con el objeto {@link UserDTO} poblado o vacío
	 *         en caso de error 404.
	 */
	@GetMapping("/getbyid/{id}")
	ResponseEntity<UserDTO> getById(@PathVariable Long id) {
		UserDTO found = userServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Recupera la información de un usuario a partir de su nombre de usuario en
	 * texto claro.
	 *
	 * @param user Nombre de usuario a buscar.
	 * @return {@link ResponseEntity} conteniendo el DTO del perfil solicitado.
	 */
	@GetMapping("/getbyuser/{user}")
	public ResponseEntity<UserDTO> getByUser(@PathVariable String user) {
		UserDTO found = userServ.getByUser(user);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Remueve de forma definitiva una cuenta de usuario del sistema basándose en su
	 * ID de persistencia.
	 *
	 * @param id Clave primaria numérita del usuario.
	 * @return {@link ResponseEntity} con mensaje aclaratorio del estado definitivo
	 *         de la supresión.
	 */
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