package com.bolsadeideas.springboot.app.view.xml;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

@Component("listar.xml")
public class ClienteListXmlView extends MarshallingView {

	@Autowired
	public ClienteListXmlView(Jaxb2Marshaller marshaller) {
		super(marshaller);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		/**
		 * Eliminamos estos atributos que vienen del controlador ClienteController,
		 * método listar, ya que se necesita que el model aquí esté limpio por que no
		 * los queremos en el XML.
		 * 
		 * Lo que se convertirá en XML según la configuración en la clase MvcConfig
		 * método jaxb2Marshaller, es la clase ClienteList, que contendrá la lista de
		 * clientes.
		 */
		model.remove("titulo");
		model.remove("page");
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		model.remove("clientes");

		model.put("clienteList", new ClienteList(clientes.getContent()));
		super.renderMergedOutputModel(model, request, response);
	}

}
