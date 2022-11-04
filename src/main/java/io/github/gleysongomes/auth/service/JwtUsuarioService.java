package io.github.gleysongomes.auth.service;

import java.util.List;
import java.util.UUID;

import io.github.gleysongomes.auth.dto.JwtUsuarioDto;

public interface JwtUsuarioService {

	JwtUsuarioDto salvar(JwtUsuarioDto jwtUsuarioDto);

	void excluir(String login);

	List<JwtUsuarioDto> listarPorLogin(String login);

	boolean existsByLogin(String login);

	boolean existsByCdJwtUsuario(UUID cdJwtUsuario);
}
