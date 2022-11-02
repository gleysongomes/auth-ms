package io.github.gleysongomes.auth.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gleysongomes.auth.dto.UsuarioEventDto;
import io.github.gleysongomes.auth.enums.TipoAcao;
import io.github.gleysongomes.auth.model.PapelUsuario;
import io.github.gleysongomes.auth.model.Usuario;
import io.github.gleysongomes.auth.publisher.UsuarioEventPublisher;
import io.github.gleysongomes.auth.repository.PapelRepository;
import io.github.gleysongomes.auth.repository.PapelUsuarioRepository;
import io.github.gleysongomes.auth.repository.UsuarioRepository;
import io.github.gleysongomes.auth.service.UsuarioService;
import io.github.gleysongomes.auth.util.Constantes;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PapelRepository papelRepository;

	@Autowired
	private UsuarioEventPublisher usuarioEventPublisher;

	@Autowired
	private PapelUsuarioRepository papelUsuarioRepository;

	@Override
	public Usuario salvar(Usuario usuario) {
		usuario = usuarioRepository.save(usuario);
		return usuario;
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		usuario = salvar(usuario);
		var papelUsuario = criarPapelUsuario(usuario);
		papelUsuarioRepository.adicionar(papelUsuario);
		usuarioEventPublisher.publishUsuarioEvent(converterUsuarioParaUsuarioEventDto(usuario), TipoAcao.SALVAR);
		return usuario;
	}

	private PapelUsuario criarPapelUsuario(Usuario usuario) {
		var papel = papelRepository.findByNome(Constantes.PAPEL_USUARIO);
		var papelUsuario = new PapelUsuario();
		papelUsuario.setUsuario(usuario);
		papelUsuario.setPapel(papel.get());
		papelUsuario.setFlAtivo(Boolean.TRUE);
		papelUsuario.setDtCadastro(LocalDateTime.now(ZoneId.of("UTC")));
		return papelUsuario;
	}

	@Override
	public void excluir(Usuario usuario) {
		usuarioRepository.delete(usuario);
		usuarioEventPublisher.publishUsuarioEvent(converterUsuarioParaUsuarioEventDto(usuario), TipoAcao.EXCLUIR);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> buscar(UUID cdUsuario) {
		return usuarioRepository.findByCdUsuario(cdUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> listar(Specification<Usuario> usuarioSpec, Pageable pageable) {
		return usuarioRepository.findAll(usuarioSpec, pageable);
	}

	@Override
	public void atualizarSenha(Usuario usuario) {
		salvar(usuario);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByLogin(String login) {
		return usuarioRepository.existsByLogin(login);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return usuarioRepository.existsByEmail(email);
	}

	private UsuarioEventDto converterUsuarioParaUsuarioEventDto(Usuario usuario) {
		var usuarioEventDto = new UsuarioEventDto();
		BeanUtils.copyProperties(usuario, usuarioEventDto);
		usuarioEventDto.setStatusUsuario(usuario.getStatusUsuario().toString());
		return usuarioEventDto;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCdUsuario(UUID cdUsuario) {
		return usuarioRepository.existsByCdUsuario(cdUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> findByLogin(String login) {
		return usuarioRepository.findByLogin(login);
	}

}
