package com.bolsadeideas.springboot.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

@Repository
public class ClienteDaoImpl implements IClienteDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Cliente> findAll() {
		return this.em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
	}

	@Override
	public Cliente findOne(Long id) {
		return this.em.find(Cliente.class, id);
	}

	@Override
	public void save(Cliente cliente) {
		if (cliente.getId() != null && cliente.getId() > 0) {
			this.em.merge(cliente);
		} else {
			this.em.persist(cliente);
		}
	}

	@Override
	public void delete(Long id) {
		Cliente cliente = this.findOne(id);
		this.em.remove(cliente);
	}

}
