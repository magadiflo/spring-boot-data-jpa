package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.services.IClienteService;
import com.bolsadeideas.springboot.app.models.services.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private MessageSource messageSource;

	// .+, permite que Spring no trunque o borre la extensión del archivo
	@Secured({"ROLE_USER"}) //Un arreglo que adminte varios roles, aquí solo hay uno
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Resource recurso = null;
		try {
			recurso = this.uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		//Cliente cliente = this.clienteService.findOne(id);
		Cliente cliente = this.clienteService.fetchByIdWithFacturas(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Detalle del cliente: " + cliente.getNombre());
		return "ver";
	}
	
	//Método REST, devuelve un objeto JSON
	@GetMapping(value = "/listar-rest")
	public @ResponseBody List<Cliente> listarRest() {
		return this.clienteService.findAll();
	}

	@RequestMapping(value = {"/", "/listar"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, 
			Authentication authentication, HttpServletRequest request, Locale locale) {
		
		if(authentication != null) {
			this.logger.info("Hola usuario authenticado, tu username es: ".concat(authentication.getName()));
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth != null) {
			this.logger.info("Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): Usuario authenticado, tu username es: ".concat(auth.getName()));
		}
		
		//3 Formas de verificar el role
		
		//1° Forma, creando manualmente el método y verificando el role
		if(this.hasRole("ROLE_ADMIN")) {
			this.logger.info("1° FORMA: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			this.logger.info("1° FORMA: Hola ".concat(auth.getName()).concat(" no tienes acceso!"));
		}
		
		//2° Forma, con el SecurityContextHolderAwareRequestWrapper
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			this.logger.info("2° FORMA: Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			this.logger.info("2° FORMA: Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat(" no tienes acceso!"));
		}
		
		//3° Forma, con el HttpServletRequest
		if(request.isUserInRole("ROLE_ADMIN")) {
			this.logger.info("3° FORMA: Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			this.logger.info("3° FORMA: Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" no tienes acceso!"));
		}
		
		Pageable pageRequest = PageRequest.of(page, 5); // 5 registros por página
		Page<Cliente> clientesPage = this.clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientesPage);

		model.addAttribute("titulo", this.messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		model.addAttribute("clientes", clientesPage);
		model.addAttribute("page", pageRender);
		return "listar";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form") // Por defecto GET
	public String crear(Map<String, Object> model) {
		Cliente cliente = new Cliente();
		model.put("titulo", "Formulario de cliente");
		model.put("cliente", cliente);
		return "form";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = this.clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El id del cliente no existe en la BD");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El id del cliente no puede ser menor o igual a cero");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar cliente");
		return "form";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			// En automático el objeto cliente pasará al formulario, siempre y cuando
			// el nombre cliente sea igual al atributo que se le pasa a la vista
			// Caso contrario, si el nombre que se le pasa a la vista es distinto
			// se usaría el @ModelAttribute("nombre_que_se_le_pasa_a_la_vista")
			model.addAttribute("titulo", "Formulario de cliente - Corregir");
			return "form";
		}

		if (!foto.isEmpty()) {
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				this.uploadFileService.delete(cliente.getFoto());
			}

			String nuevoNombreArchivo = null;
			try {
				nuevoNombreArchivo = this.uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Has subido correctamente '" + nuevoNombreArchivo + "'");

			// Asignando foto al cliente para guardar en la BD
			cliente.setFoto(nuevoNombreArchivo);
		}

		String msg = cliente.getId() != null ? "Cliente actualizado con éxito" : "Cliente creado con éxito";
		this.clienteService.save(cliente);
		status.setComplete(); // Elimina el obj. cliente de la sesión (se declarado al inicio de la clase)

		flash.addFlashAttribute("success", msg);
		return "redirect:/listar";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = this.clienteService.findOne(id);

			this.clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito");

			if (cliente.getFoto() != null && cliente.getFoto().length() > 0) {
				if (this.uploadFileService.delete(cliente.getFoto())) {
					flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con éxito!");
				}
			}

		}
		return "redirect:/listar";
	}
	
	public boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if(context == null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		if(auth == null) {
			return false;
		}
		
		//? extends GrantedAuthority: Singifica "Cualquier tipo de objeto que herede de GrantedAuthority"
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		/*** PRIMERA FORMA
		for(GrantedAuthority authority: authorities) {
			if(role.equals(authority.getAuthority())) {
				this.logger.info("Hola usuario".concat(auth.getName()).concat(" tu role es: ").concat(authority.getAuthority()));
				return true;	
			}
		}
		return false;
		*/
		
		//*** SEGUNDA FORMA
		return authorities.contains(new SimpleGrantedAuthority(role));
	}

}
