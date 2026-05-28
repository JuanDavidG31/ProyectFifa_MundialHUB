package co.edu.unbosque.projectFifaUbosque.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para la integración con el servicio de almacenamiento
 * en la nube Cloudinary.
 * <p>
 * Se encarga de leer las credenciales desde las propiedades de la aplicación e
 * inicializar el bean oficial de Cloudinary para la gestión de archivos
 * multimedia (como avatares de usuarios).
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Configuration
public class CloudinaryConfig {
	/** Nombre del cloud space en Cloudinary asignado a la aplicación. */
	@Value("${cloudinary.cloud_name}")
	private String cloudName;
	/** Llave pública de la API de Cloudinary. */
	@Value("${cloudinary.api_key}")
	private String apiKey;
	/** Clave secreta para la firma y autenticación de peticiones en Cloudinary. */
	@Value("${cloudinary.api_secret}")
	private String apiSecret;

	/**
	 * Crea y configura una instancia única (Bean) de {@link Cloudinary} utilizando
	 * las credenciales inyectadas.
	 *
	 * @return Una instancia configurada de {@link Cloudinary} lista para
	 *         interactuar con la API.
	 */
	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
	}
}