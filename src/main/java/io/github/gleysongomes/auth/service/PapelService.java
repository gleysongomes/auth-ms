package io.github.gleysongomes.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import io.github.gleysongomes.auth.model.Papel;

public interface PapelService {

	Papel salvar(Papel papel);

	void atualizar(Papel papel);

	void excluir(UUID cdPapel);

	Optional<Papel> buscar(UUID cdPapel);

	Page<Papel> listar(Specification<Papel> papelSpec, Pageable pageable);

	boolean existsByCdPapel(UUID cdPapel);

	boolean existsByNome(String nome);
}
