/*-- 
Sección: 14. Spring Security Authenticación JDBC - Base de datos 
Tema: Creando el esquema de tablas de base de datos --*/
CREATE TABLE users(
	id INT AUTO_INCREMENT,
	username VARCHAR(45) NOT NULL,
	password VARCHAR(60) NOT NULL,
	enabled TINYINT(1) NOT NULL DEFAULT 1,
	CONSTRAINT uq_users_username UNIQUE(username),
	CONSTRAINT pk_users_id PRIMARY KEY(id)
);

CREATE TABLE authorities(
	id INT AUTO_INCREMENT,
	user_id INT NOT NULL,
	authority VARCHAR(45) NOT NULL,
	CONSTRAINT pk_authorities PRIMARY KEY(id),
	CONSTRAINT fk_users_authorities FOREIGN KEY(user_id) REFERENCES users(id) 
		ON DELETE CASCADE 
		ON UPDATE CASCADE,
	CONSTRAINT uq_authorities__user_id_authority UNIQUE(user_id, authority)
);

INSERT INTO users(username, PASSWORD, enabled) VALUES('magadiflo', '$2a$10$7.GlaegRST2vK5rzoqwzEOWLiT0a7lUdDw3q8/WVOfdFXCv4MEqGq', 1);
INSERT INTO users(username, PASSWORD, enabled) VALUES('admin', '$2a$10$sI1L6GBou/n.jHGoRLogGeICjlIaMf2HjxuJrGdwhRvNDHvAdH2q.', 1);

INSERT INTO authorities(user_id, authority) VALUES(1, 'ROLE_USER');
INSERT INTO authorities(user_id, authority) VALUES(2, 'ROLE_USER');
INSERT INTO authorities(user_id, authority) VALUES(2, 'ROLE_ADMIN');