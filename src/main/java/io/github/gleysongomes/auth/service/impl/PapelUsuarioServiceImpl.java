package io.github.gleysongomes.auth.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gleysongomes.auth.model.PapelUsuario;
import io.github.gleysongomes.auth.repository.PapelUsuarioRepository;
import io.github.gleysongomes.auth.service.PapelUsuarioService;

@Service
@Transactional
public class PapelUsuarioServiceImpl implements PapelUsuarioService {

	@Autowired
	private PapelUsuarioRepository papelUsuarioRepository;

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCdPapelAndCdUsuario(UUID cdPapel, UUID cdUsuario) {
		return papelUsuarioRepository.existsByCdPapelAndCdUsuario(cdPapel, cdUsuario);
	}

	@Override
	public void excluir(UUID cdPapel, UUID cdUsuario) {
		papelUsuarioRepository.excluir(cdPapel, cdUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PapelUsuario> buscar(UUID cdPapel, UUID cdUsuario) {
		return papelUsuarioRepository.buscar(cdPapel, cdUsuario);
	}

	@Override
	public void atualizar(PapelUsuario papelUsuario) {
		papelUsuarioRepository.atualizar(papelUsuario);
	}

	@Override
	public void adicionar(PapelUsuario papelUsuario) {
		papelUsuarioRepository.adicionar(papelUsuario);
	}

}
