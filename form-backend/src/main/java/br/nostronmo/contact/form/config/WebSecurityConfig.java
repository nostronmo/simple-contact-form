package br.nostronmo.contact.form.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import br.nostronmo.contact.form.exception.global.AuthEntryPointJwt;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final CorsConfigurationSource corsConfigurationSource;
	private final AuthEntryPointJwt unauthorizedHandler;

	@Bean
	@Order(1)
	SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/h2-console/**").authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.csrf(AbstractHttpConfigurer::disable)
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
		return http.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler));

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/int/**").access((authentication, context) -> {
					String ip = context.getRequest().getRemoteAddr();
					return new AuthorizationDecision(ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1"));
				})
				.anyRequest().permitAll()
		);

		http.csrf(AbstractHttpConfigurer::disable);
		http.formLogin(AbstractHttpConfigurer::disable);
		http.httpBasic(AbstractHttpConfigurer::disable);
		http.logout(AbstractHttpConfigurer::disable);

		return http.build();
	}
}
