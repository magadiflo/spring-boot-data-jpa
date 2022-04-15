## Habilitar la consola para trabajar con BD H2 (en memoria)
En el archivo application.properties agregar

```
spring.h2.console.enabled=true
```

Ejecutar el proyecto y en la url ingresar

```
http://localhost:8080/h2-console/
```

Por defecto el user es: sa y la contraseña es vacía, la conexión JDBC URL: la debemos buscar en la consola, 
similar a esta (la de abajo) y reemplazarla por el que viene por defecto. 
En versiones anteriores a Spring Boot 2.3.0 se 
hacía la conexión defrente. Posterior a esa versión tenemos que hacer este paso

```
jdbc:h2:mem:a92fb3af-ae31-4cb3-aa99-84cc46641970
```

## Recordar
Se puede establecer una configuración personalizada en el archivo application.properties

## iText (Generación de PDF)
```
https://api.itextpdf.com/iText5/java/5.5.13.3/
```

## Excel (xlsx)
```
https://poi.apache.org/
```