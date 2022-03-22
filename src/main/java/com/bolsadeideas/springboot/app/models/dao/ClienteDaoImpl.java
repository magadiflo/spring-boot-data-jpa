package com.bolsadeideas.springboot.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

@Repository
public class ClienteDaoImpl implements IClienteDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {
		return this.em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
	}

	@Override
	@Transactional
	public void save(Cliente cliente) {
		if(cliente.getId() != null && cliente.getId() > 0) {
			this.em.merge(cliente);
		} else {
			this.em.persist(cliente);			
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Cliente findOne(Long id) {
		return this.em.find(Cliente.class, id);
	}

}
