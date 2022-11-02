package io.github.gleysongomes.auth.service;

import java.util.Optional;
import java.util.UUID;

import io.github.gleysongomes.auth.model.PapelUsuario;

public interface PapelUsuarioService {

	boolean existsByCdPapelAndCdUsuario(UUID cdPapel, UUID cdUsuario);

	void excluir(UUID cdPapel, UUID cdUsuario);

	Optional<PapelUsuario> buscar(UUID cdPapel, UUID cdUsuario);

	void atualizar(PapelUsuario papelUsuario);

	void adicionar(PapelUsuario papelUsuario);
}
