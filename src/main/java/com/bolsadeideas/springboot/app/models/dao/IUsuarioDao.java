package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {

	//Como el nombre del método tiene la conveción del Query Method Name,
	//es que se ejecutará la consulta JPQL
	//SELECT u FROM Usuario AS u WHERE u.username = ?1
	public Usuario findByUsername(String username);

}
