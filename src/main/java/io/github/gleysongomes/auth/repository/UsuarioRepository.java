package io.github.gleysongomes.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.github.gleysongomes.auth.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {

	boolean existsByLogin(String email);

	boolean existsByEmail(String email);

	boolean existsByCdUsuario(UUID cdUsuario);

	@EntityGraph(attributePaths = "papeis")
	Optional<Usuario> findByCdUsuario(UUID cdUsuario);

	Optional<Usuario> findByLogin(String login);
}
