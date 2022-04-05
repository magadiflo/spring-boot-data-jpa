package com.bolsadeideas.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;
import com.bolsadeideas.springboot.app.models.entity.Producto;
import com.bolsadeideas.springboot.app.models.services.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable Long clienteId, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = this.clienteService.findOne(clienteId);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}

		Factura factura = new Factura();
		factura.setCliente(cliente);

		model.put("factura", factura);
		model.put("titulo", "Crear factura");

		return "factura/form";
	}

	/**
	 * @ResponseBody, suprime el cargar una vista de thymeleaf y en ves de eso, el
	 * resultado lo estará retornando convertido en JSON y eso lo va a registrar
	 * dentro del Body de la respuesta
	 */
	@GetMapping(value = "/cargar-productos/{term}", produces = { "application/json" })
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
		return this.clienteService.findByNombre(term);
	}
	
	@PostMapping("/form")
	public String guardar(@Valid Factura factura, BindingResult result,
			Model model,
			@RequestParam(name = "item-id[]", required = false) Long itemId[], 
			@RequestParam(name = "cantidad[]", required = false) Integer cantidad[],
			RedirectAttributes flash, 
			SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Crear factura (se detectaron errores)");
			return "factura/form";
		}
		
		if(itemId == null || itemId.length == 0) {
			model.addAttribute("titulo", "Crear factura (se detectaron errores)");
			model.addAttribute("error", "Error: La factura debe tener al menos una línea!");
			return "factura/form";			
		}
		
		for(int i = 0; i < itemId.length; i++) {
			Producto producto = this.clienteService.findProductoById(itemId[i]);
			ItemFactura linea = new ItemFactura();
			
			if(producto != null) {
				linea.setProducto(producto);
				linea.setCantidad(cantidad[i]);
				
				factura.addItemFactura(linea);
			}
			
			this.log.info("ID: " + itemId[i].toString() + ", cantidad: " + cantidad[i]);
		}
		
		this.clienteService.saveFactura(factura);
		
		//Finalizamos el @SessionAttributes("factura")
		status.setComplete();
		
		flash.addFlashAttribute("success", "Factura creada con éxito!");
		
		return "redirect:/ver/" + factura.getCliente().getId();
	}
	
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
		
		//Con esto se realiza una única consulta para que traiga los datos de 
		//toda la relación: cliente, factura, itemFactura y producto.
		//Con esto ya no hay carga perezosa (lazy)
		Factura factura = this.clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id);
		
		if(factura == null) {
			flash.addFlashAttribute("error", "La factura no existe en la base de datos");
			return "redirect:/listar";
		}
		
		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Factura: ".concat(factura.getDescripcion()));
		
		return "factura/ver";
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		Factura factura = this.clienteService.findFacturaById(id);
		if(factura != null) {
			this.clienteService.deleteFactura(id);
			flash.addFlashAttribute("success", "Factura eliminada con éxito");
			return "redirect:/ver/" + factura.getCliente().getId();
		}
		flash.addFlashAttribute("error", "La factura no existe en la base de datos. No se pudo eliminar.");
		return "redirect:/listar";
	}
	

}
