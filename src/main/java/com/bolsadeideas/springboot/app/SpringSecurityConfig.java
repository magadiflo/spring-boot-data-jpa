package com.bolsadeideas.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private LoginSuccessHandler successHandler; //Nos permitirá enviar el mensaje de que se inició sesión

	// @Bean: para guardar el objeto creado con new BCryptPasswordEncoder() en el
	// contenedor. Esto será usado por defecto por Spring Security
	// >>>> static, se colocó static por que mostraba error de que hay una
	// dependencia circular
	// >>>> ya que SpringSecurityConfig es un bean en sí misma que necesita de la
	// instancia completa
	// >>>> del bean BCryptPasswordEncoder, y este último necesita a su vez
	// >>>> una intancia de SpringSecurityConfig
	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// @Autowired Para poder inyectar el AuthenticationManagerBuilder
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {
		PasswordEncoder encoder = SpringSecurityConfig.passwordEncoder();
		// passwordEncoder(encoder::encode): Expresión que sería equivalente a:
		// passwordEncoder(password -> encoder.encode(password))
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);

		builder.inMemoryAuthentication()
				.withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
				.withUser(users.username("magadiflo").password("12345").roles("USER"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
				.antMatchers("/ver/**").hasAnyRole("USER")
				.antMatchers("/uploads/**").hasAnyRole("USER")
				.antMatchers("/form/**").hasAnyRole("ADMIN")
				.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
				.antMatchers("/factura/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated()
				.and()
				.formLogin().successHandler(this.successHandler).loginPage("/login").permitAll()
				.and()
				.logout().permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/error_403"); //Esta ruta está configurada en el MvcConfig

	}

}
