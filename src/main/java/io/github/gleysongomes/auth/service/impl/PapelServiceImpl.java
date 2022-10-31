package io.github.gleysongomes.auth.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gleysongomes.auth.model.Papel;
import io.github.gleysongomes.auth.repository.PapelRepository;
import io.github.gleysongomes.auth.service.PapelService;

@Service
@Transactional
public class PapelServiceImpl implements PapelService {

	@Autowired
	private PapelRepository papelRepository;

	@Override
	public Papel salvar(Papel papel) {
		return papelRepository.save(papel);
	}

	@Override
	public void atualizar(Papel papel) {
		papelRepository.atualizar(papel);
	}

	@Override
	public void excluir(UUID cdPapel) {
		papelRepository.excluir(cdPapel);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Papel> buscar(UUID cdPapel) {
		return papelRepository.buscar(cdPapel);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Papel> listar(Specification<Papel> papelSpec, Pageable pageable) {
		return papelRepository.findAll(papelSpec, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCdPapel(UUID cdPapel) {
		return papelRepository.existsByCdPapel(cdPapel);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByNome(String nome) {
		return papelRepository.existsByNome(nome);
	}

}
