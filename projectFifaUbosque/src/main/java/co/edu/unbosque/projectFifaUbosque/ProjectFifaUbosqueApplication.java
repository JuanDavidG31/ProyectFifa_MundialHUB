package co.edu.unbosque.projectFifaUbosque;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Clase principal de arranque y entrada del proyecto de backend FIFA Ubosque
 * 2026.
 * <p>
 * Al usar la anotación {@link SpringBootApplication}, activa de manera
 * automática tres características esenciales del framework: la configuración
 * automática de componentes (Auto-Configuration), el escaneo de componentes
 * (Component Scan) en todo el paquete base, y la capacidad de registrar beans
 * de configuración adicionales en el contexto de Spring.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@SpringBootApplication
public class ProjectFifaUbosqueApplication {
	/**
	 * Método de entrada (Entry Point) principal que inicializa y arranca la
	 * aplicación de Spring Boot.
	 * <p>
	 * Delegará el control del ciclo de vida de la aplicación al motor interno de
	 * {@link SpringApplication}, levantando el servidor embebido (generalmente
	 * Tomcat) en el puerto configurado.
	 * </p>
	 *
	 * @param args Argumentos opcionales de la línea de comandos pasados al arrancar
	 *             el proceso.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProjectFifaUbosqueApplication.class, args);

	}

	/**
	 * Declara y produce una instancia única (Bean) de {@link ModelMapper} en el
	 * contexto global de Spring.
	 * <p>
	 * ModelMapper es un componente de mapeo de objetos diseñado para simplificar
	 * las transformaciones y conversiones automáticas entre diferentes modelos de
	 * datos, tales como la conversión mutua entre entidades JPA (Model) y Objetos
	 * de Transferencia de Datos (DTOs).
	 * </p>
	 *
	 * @return Una instancia compartida y lista para inyección de dependencia de
	 *         {@link ModelMapper}.
	 */
	@Bean
	ModelMapper getModelMapper() {
		return new ModelMapper();
	}

}
