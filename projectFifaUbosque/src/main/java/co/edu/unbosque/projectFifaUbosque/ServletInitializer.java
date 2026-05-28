package co.edu.unbosque.projectFifaUbosque;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Inicializador de Servlet para el despliegue de la aplicación en contenedores
 * externos.
 * <p>
 * Esta clase hereda de {@link SpringBootServletInitializer} y es fundamental
 * cuando la aplicación se compila en formato de archivo WAR (Web Application
 * Archive) para ser ejecutada dentro de un servidor de aplicaciones tradicional
 * externo, tal como Apache Tomcat, WildFly o GlassFish. Configura la aplicación
 * vinculando la clase principal de Spring Boot.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
public class ServletInitializer extends SpringBootServletInitializer {
	/**
	 * Configura la aplicación asociando la clase fuente de arranque principal de
	 * Spring Boot cuando el ciclo de vida del servlet es administrado por un
	 * contenedor web externo.
	 *
	 * @param application Un constructor {@link SpringApplicationBuilder} utilizado
	 *                    para orquestar la aplicación.
	 * @return El SpringApplicationBuilder configurado con la clase de arranque del
	 *         proyecto FIFA Ubosque.
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ProjectFifaUbosqueApplication.class);
	}

}
