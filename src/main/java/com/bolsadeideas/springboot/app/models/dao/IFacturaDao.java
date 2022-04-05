package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long> {
	
	//Consulta que traerá en una sola toda la relación de cliente, factura, itemFactura y producto
	@Query("SELECT f FROM Factura AS f JOIN FETCH f.cliente AS c JOIN FETCH f.items AS l JOIN FETCH l.producto WHERE f.id = ?1")
	public Factura fetchByIdWithClienteWithItemFacturaWithProducto(Long id);

}
