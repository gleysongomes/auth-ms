package io.github.gleysongomes.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import io.github.gleysongomes.auth.model.Usuario;

public interface UsuarioService {

	Usuario salvar(Usuario usuario);

	Usuario salvarUsuario(Usuario usuario);

	void excluir(Usuario usuario);

	Optional<Usuario> buscar(UUID cdUsuario);

	Page<Usuario> listar(Specification<Usuario> usuarioSpec, Pageable pageable);

	void atualizarSenha(Usuario usuario);

	boolean existsByLogin(String login);

	boolean existsByEmail(String email);

	boolean existsByCdUsuario(UUID cdUsuario);
}
