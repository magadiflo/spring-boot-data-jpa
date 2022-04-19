package com.bolsadeideas.springboot.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

@XmlRootElement(name = "clientes") // Elemento raíz principal que contendrá todos los datos
public class ClienteList {

	@XmlElement(name = "cliente") // Cada registro de cliente estará delimitado por la etiqueta "cliente"
	public List<Cliente> clientes;

	public ClienteList() {

	}

	public ClienteList(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

}

//Clase wrapper que contendrá lista de clientes