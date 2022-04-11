package com.bolsadeideas.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
/*import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;*/
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;

//Habilitará el uso de anotaciones que usamos en los controladores para verificar el role
//securedEnabled = true: Para usar la anotación @Secured("ROLE_USER")
//pre = true, para usar la anotación @PreAuthorize("hasRole('ROLE_USER')")
//CONCLUSIÓN: Ambas funcionan para lo mismo
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) 
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private LoginSuccessHandler successHandler; //Nos permitirá enviar el mensaje de que se inició sesión
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// @Bean: para guardar el objeto creado con new BCryptPasswordEncoder() en el
	// contenedor. Esto será usado por defecto por Spring Security
	// >>>> static, se colocó static por que mostraba error de que hay una
	// dependencia circular
	// >>>> ya que SpringSecurityConfig es un bean en sí misma que necesita de la
	// instancia completa
	// >>>> del bean BCryptPasswordEncoder, y este último necesita a su vez
	// >>>> una intancia de SpringSecurityConfig
	
	////////////////////// Esta Método lo moveremos a la clase MvcConfig, pero da lo mismo
	//Lo movemos para mostrar que se puede inyectar con @Autowired
	//@Bean
	//public static BCryptPasswordEncoder passwordEncoder() {
	//	return new BCryptPasswordEncoder();
	//}

	// @Autowired Para poder inyectar el AuthenticationManagerBuilder
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {
		/*
		PasswordEncoder encoder = this.passwordEncoder;
		// passwordEncoder(encoder::encode): Expresión que sería equivalente a:
		// passwordEncoder(password -> encoder.encode(password))
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);

		builder.inMemoryAuthentication()
				.withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
				.withUser(users.username("magadiflo").password("12345").roles("USER"));
		*/
		
		builder.jdbcAuthentication()
			.dataSource(this.dataSource)
			.passwordEncoder(this.passwordEncoder)
			.usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username = ?")
			.authoritiesByUsernameQuery("SELECT u.username, a.authority  FROM authorities AS a INNER JOIN users AS u ON a.user_id = u.id WHERE u.username = ?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
				//.antMatchers("/ver/**").hasAnyRole("USER")
				//.antMatchers("/uploads/**").hasAnyRole("USER")
				//.antMatchers("/form/**").hasAnyRole("ADMIN")
				//.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
				//.antMatchers("/factura/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated()
				.and()
				.formLogin().successHandler(this.successHandler).loginPage("/login").permitAll()
				.and()
				.logout().permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/error_403"); //Esta ruta está configurada en el MvcConfig

	}

}
