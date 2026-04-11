package co.edu.unbosque.projectFifaUbosque.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {

		final String securitySchemeName = "bearerAuth";

		String mainDescription = "<h2>API FIFA Ubosque 2026</h2>"
				+ "<p>Plataforma backend diseñada para gestionar la experiencia digital del mundial 2026.</p>"
				+ "<h3>Módulos soportados:</h3>" + "<ul>"
				+ "<li><strong>Autenticación:</strong> Seguridad basada en JWT.</li>"
				+ "<li><strong>Álbum Digital:</strong> Colección, paquetes, intercambio de láminas y gestión de moneda.</li>"
				+ "<li><strong>Pollas:</strong> Creación de comunidades, apuestas sociales y rankings.</li>"
				+ "<li><strong>Entradas:</strong> Ciclo de vida completo: Reserva, Pago (Stripe/Mock), Transferencia y Reembolso.</li>"
				+ "<li><strong>Eventos:</strong> Integración de datos en tiempo real y notificaciones.</li>" + "</ul>";

		Info info = new Info().title("FIFA Ubosque API Documentation").version("1.0").description(mainDescription)
				.contact(new Contact().name("Equipo de Desarrollo - FIFA Ubosque")
						.url("https://github.com/tu-usuario/projectFifaUbosque"))
				.license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"));

		SecurityScheme securityScheme = new SecurityScheme().name(securitySchemeName).type(SecurityScheme.Type.HTTP)
				.scheme("bearer").bearerFormat("JWT")
				.description("Introduce tu token JWT aquí para acceder a los endpoints protegidos.");

		return new OpenAPI().info(info).addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(new Components().addSecuritySchemes(securitySchemeName, securityScheme)
						.addResponses("UnauthorizedError",
								createApiResponse("No autenticado - Token inválido o expirado"))
						.addResponses("ForbiddenError",
								createApiResponse("Acceso prohibido - No tienes permisos suficientes"))
						.addResponses("NotFoundError", createApiResponse("Recurso no encontrado")));
	}

	private ApiResponse createApiResponse(String description) {
		return new ApiResponse().description(description).content(new Content().addMediaType("application/json",
				new MediaType().addExamples("error", new Example().value("{\"error\": \"" + description + "\"}"))));
	}
}