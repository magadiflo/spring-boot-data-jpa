package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.services.IClienteService;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(Model model) {
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", this.clienteService.findAll());
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
	public String editar(@PathVariable Long id, Map<String, Object> model) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = this.clienteService.findOne(id);
		} else {
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar cliente");
		return "form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, SessionStatus status) {
		if (result.hasErrors()) {
			// En automático el objeto cliente pasará al formulario, siempre y cuando
			// el nombre cliente sea igual al atributo que se le pasa a la vista
			// Caso contrario, si el nombre que se le pasa a la vista es distinto
			// se usaría el @ModelAttribute("nombre_que_se_le_pasa_a_la_vista")
			model.addAttribute("titulo", "Formulario de cliente - Corregir");
			return "form";
		}
		this.clienteService.save(cliente);
		status.setComplete(); // Elimina el obj. cliente de la sesión (se declarado al inicio de la clase)
		return "redirect:/listar";
	}

	@RequestMapping(value = "/eliminar/{id}")
	public String delete(@PathVariable Long id) {
		if (id > 0) {
			this.clienteService.delete(id);
		}
		return "redirect:/listar";
	}

}

