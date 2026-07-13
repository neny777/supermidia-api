package br.com.supermidia.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.supermidia.security.CustomAuthenticationEntryPoint;
import br.com.supermidia.security.JwtAuthenticationFilter;
import br.com.supermidia.security.Permissoes;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final List<String> allowedOrigins;
	private final List<String> allowedMethods;
	private final List<String> allowedHeaders;
	private final boolean allowCredentials;

	public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
			AuthenticationProvider authenticationProvider, CustomAuthenticationEntryPoint authenticationEntryPoint,
			@Value("${app.cors.allowed-origins:http://localhost:3000,http://192.168.3.10:3000}") List<String> allowedOrigins,
			@Value("${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}") List<String> allowedMethods,
			@Value("${app.cors.allowed-headers:*}") List<String> allowedHeaders,
			@Value("${app.cors.allow-credentials:true}") boolean allowCredentials) {
		this.authenticationProvider = authenticationProvider;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.allowedOrigins = allowedOrigins;
		this.allowedMethods = allowedMethods;
		this.allowedHeaders = allowedHeaders;
		this.allowCredentials = allowCredentials;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
				// Permitir endpoints públicos
				.requestMatchers("/api/health").permitAll().requestMatchers("/api/authentication/**").permitAll()
				.requestMatchers("/api/usuarios/validate-token").permitAll().requestMatchers("/api/password/**")
				.permitAll().requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permitir OPTIONS
				.requestMatchers("/api/pessoas/**")
				.hasAnyRole(Permissoes.CLIENTES, Permissoes.COLABORADORES, Permissoes.FORNECEDORES, Permissoes.PARCEIROS)
				// Quem cadastra usuários precisa listar colaboradores sem login
				.requestMatchers("/api/colaboradores/nao-usuario")
				.hasAnyRole(Permissoes.USUARIOS, Permissoes.COLABORADORES)
				.requestMatchers("/api/colaboradores/**").hasRole(Permissoes.COLABORADORES)
				.requestMatchers("/api/usuarios/**").hasRole(Permissoes.USUARIOS)
				.requestMatchers("/api/clientes/**").hasRole(Permissoes.CLIENTES)
				.requestMatchers("/api/fornecedores/**").hasRole(Permissoes.FORNECEDORES)
				.requestMatchers("/api/parceiros/**").hasRole(Permissoes.PARCEIROS)
				.requestMatchers("/api/materias/**").hasRole(Permissoes.MATERIAS)
				.requestMatchers("/api/servicos/**").hasRole(Permissoes.SERVICOS)
				.requestMatchers("/api/calculos/**").hasRole(Permissoes.PRODUTOS)
				.requestMatchers("/api/produtos/**").hasRole(Permissoes.PRODUTOS)
				.requestMatchers("/api/vendas/**").hasRole(Permissoes.VENDAS)
				.requestMatchers("/api/configuracoes/**").hasRole(Permissoes.CONFIGURACOES)
				// Qualquer endpoint de API sem regra específica exige autenticação.
				.requestMatchers("/api/**").authenticated()
				// O restante é o frontend (arquivos estáticos + rotas da SPA).
				.anyRequest().permitAll())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(allowedOrigins);
		configuration.setAllowedMethods(allowedMethods);
		configuration.setAllowedHeaders(allowedHeaders);
		configuration.setAllowCredentials(allowCredentials);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
