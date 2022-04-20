package com.bolsadeideas.springboot.app.view.json;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

/**
 * listar, es el nombre de la vista que devuelve el método listar del
 * ClienteController, le agregamos la extensión .json para diferenciarlo de
 * otros componentes que también usan esa vista, como el xml o el csv
 *
 */
@Component("listar.json")
public class ClienteListJsonView extends MappingJackson2JsonView {

	@SuppressWarnings("unchecked")
	@Override
	protected Object filterModel(Map<String, Object> model) {
		model.remove("titulo");
		model.remove("page");

		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		model.remove("clientes");

		List<Cliente> listaClientes = clientes.getContent();
		model.put("clientes", listaClientes);
		return super.filterModel(model);
	}

}
