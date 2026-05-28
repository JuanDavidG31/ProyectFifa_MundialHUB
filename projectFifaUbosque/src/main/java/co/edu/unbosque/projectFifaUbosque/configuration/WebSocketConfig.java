package co.edu.unbosque.projectFifaUbosque.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import co.edu.unbosque.projectFifaUbosque.security.JwtUtil;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;

/**
 * Configuración de mensajería asíncrona bidireccional basada en WebSockets con
 * protocolo STOMP.
 * <p>
 * Implementa interceptores en los canales de entrada del cliente para validar
 * tokens JWT en solicitudes de conexión entrante y asocia la sesión al contexto
 * de seguridad de Spring Security.
 * </p>
 *
 * @author Equipo de Desarrollo - FIFA Ubosque
 * @version 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	/** Componente para la extracción y validación de JSON Web Tokens. */
	@Autowired
	private JwtUtil jwtUtil;
	/**
	 * Servicio para la búsqueda de información de credenciales y authorities de
	 * usuarios.
	 */
	@Autowired
	private UserDetailsService userDetailsService;
	/**
	 * Dominios u orígenes CORS permitidos para las conexiones entrantes de
	 * WebSocket.
	 */
	@Value("${websocket.allowed-origins:https://proyectfifa2026.netlify.app}")
	private String allowedOrigins;

	/**
	 * Configura el bróker de mensajería interna. Habilita rutas de cola destino,
	 * prefijos de aplicación y direccionamiento personalizado por usuario único.
	 *
	 * @param config Registro del bróker de mensajería proporcionado por Spring.
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/queue");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}

	/**
	 * Registra los endpoints STOMP HTTP mapeados en la aplicación, vinculando
	 * políticas CORS y SockJS.
	 *
	 * @param registry Registro de endpoints de mensajería entrante.
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOriginPatterns(allowedOrigins).withSockJS();
	}

	/**
	 * Agrega un interceptor al canal de entrada para validar la autenticidad del
	 * usuario en la petición 'CONNECT'. Descifra la identidad del cliente extraída
	 * desde el token Jwt antes de ser entregado al bróker.
	 *
	 * @param registration Objeto de registro para configurar interceptores de
	 *                     canales.
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					String authHeader = accessor.getFirstNativeHeader("Authorization");
					if (authHeader != null && authHeader.startsWith("Bearer ")) {
						String token = authHeader.substring(7);
						String encryptedUsername = jwtUtil.extractUsername(token);

						if (encryptedUsername != null) {
							UserDetails userDetails = userDetailsService.loadUserByUsername(encryptedUsername);
							if (jwtUtil.validateToken(token, userDetails)) {
								String cleanUsername = AESUtil.decrypt(encryptedUsername);
								UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
										cleanUsername, null, userDetails.getAuthorities());
								SecurityContextHolder.getContext().setAuthentication(auth);
								accessor.setUser(auth);
							}
						}
					}
				}
				return message;
			}
		});
	}
}