package com.bolsadeideas.springboot.app;

import org.springframework.context.annotation.Bean;

//import java.nio.file.Paths;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	//private final Logger log = LoggerFactory.getLogger(getClass());

	// Para agregar recursos estáticos a nuestro proyecto
	/*@Override >>>>>>> Se cargarán las imágenes programáticamente en la respuesta HTTP
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		WebMvcConfigurer.super.addResourceHandlers(registry);

		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
		log.info("resourcePath: " + resourcePath);
		registry
			.addResourceHandler("/uploads/**") //Desde la vista se accede a través de esta ruta, es decir lo configuramos como un directorio static
			.addResourceLocations(resourcePath); //Esta es la ruta física donde están las imágenes. La carpeta la creamos en la raíz del proyecto

	}
	*/
	
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/error_403").setViewName("error_403");		
	}
	
	/////////////////Este método fue movido de la clase SpringSecurityConfig
	/////////////////para ver que también puede ser inyectado en esa clase
	/////////////////con el @Autowired ya que este método tiene la anotación
	////////////////del tipo @Bean
	// @Bean: para guardar el objeto creado con new BCryptPasswordEncoder() en el
	// contenedor. Esto será usado por defecto por Spring Security
	// >>>> static, se colocó static por que mostraba error de que hay una
	// dependencia circular
	// >>>> ya que SpringSecurityConfig es un bean en sí misma que necesita de la
	// instancia completa
	// >>>> del bean BCryptPasswordEncoder, y este último necesita a su vez
	// >>>> una intancia de SpringSecurityConfig
	@Bean
	public BCryptPasswordEncoder passwordEncoder() { //static: Aquí ya no hay problema con la dependencia circular po eso no colocamos static
		return new BCryptPasswordEncoder();
	}

}
