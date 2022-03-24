package com.bolsadeideas.springboot.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	// Para agregar recursos estáticos a nuestro proyecto
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		WebMvcConfigurer.super.addResourceHandlers(registry);

		registry
			.addResourceHandler("/uploads/**") //Desde la vista se accede a través de esta ruta, es decir lo configuramos como un directorio static
			.addResourceLocations("file:/C:/temp/uploads/"); //Esta es la ruta física donde están las imágenes. Se debe crear la carpeta manualmente

	}

}
