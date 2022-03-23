package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.services.IClienteService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = this.clienteService.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Detalle del cliente: " + cliente.getNombre());
		return "ver";
	}

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		Pageable pageRequest = PageRequest.of(page, 5); // 5 registros por página
		Page<Cliente> clientesPage = this.clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientesPage);

		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientesPage);
		model.addAttribute("page", pageRender);
		return "listar";
	}

	@RequestMapping(value = "/form") // Por defecto GET
	public String crear(Map<String, Object> model) {
		Cliente cliente = new Cliente();
		model.put("titulo", "Formulario de cliente");
		model.put("cliente", cliente);
		return "form";
	}

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
			Path directorioRecursos = Paths.get("src//main//resources//static//uploads");
			String rootPath = directorioRecursos.toFile().getAbsolutePath();
			try {
				byte[] bytes = foto.getBytes();
				// --Martín
				int lastIndex = foto.getOriginalFilename().lastIndexOf(".");
				String extensionArchivo = foto.getOriginalFilename().substring(lastIndex);
				UUID uuid = UUID.randomUUID();
				String nuevoNombreArchivo = uuid.toString() + extensionArchivo;
				Path rutaCompleta = Paths.get(rootPath + "//" + nuevoNombreArchivo);
				// --
				Files.write(rutaCompleta, bytes);
				flash.addFlashAttribute("info", "Has subido correctamente '" + nuevoNombreArchivo + "'");

				// Asignando foto al cliente para guardar en la BD
				cliente.setFoto(nuevoNombreArchivo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String msg = cliente.getId() != null ? "Cliente actualizado con éxito" : "Cliente creado con éxito";
		this.clienteService.save(cliente);
		status.setComplete(); // Elimina el obj. cliente de la sesión (se declarado al inicio de la clase)

		flash.addFlashAttribute("success", msg);
		return "redirect:/listar";
	}

	@RequestMapping(value = "/eliminar/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes flash) {
		if (id > 0) {
			this.clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito");
		}
		return "redirect:/listar";
	}

}
