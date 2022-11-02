package io.github.gleysongomes.auth.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.gleysongomes.auth.dto.PapelUsuarioDto;
import io.github.gleysongomes.auth.dto.UsuarioDto;
import io.github.gleysongomes.auth.enums.StatusUsuario;
import io.github.gleysongomes.auth.model.Papel;
import io.github.gleysongomes.auth.model.PapelUsuario;
import io.github.gleysongomes.auth.model.Usuario;
import io.github.gleysongomes.auth.service.PapelService;
import io.github.gleysongomes.auth.service.PapelUsuarioService;
import io.github.gleysongomes.auth.service.UsuarioService;
import io.github.gleysongomes.auth.specification.SpecificationTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PapelService papelService;

	@Autowired
	private PapelUsuarioService papelUsuarioService;

	@GetMapping
	public ResponseEntity<Page<Usuario>> listar(SpecificationTemplate.UsuarioSpec usuarioSpec,
			@PageableDefault(page = 0, size = 10, sort = "dtCadastro", direction = Sort.Direction.DESC) Pageable pageable) {
		log.debug("Listar usuários utilizado filtros e paginação: {} - {}", usuarioSpec, pageable);
		Page<Usuario> usuarioPage = usuarioService.listar(usuarioSpec, pageable);
		if (!usuarioPage.isEmpty()) {
			for (Usuario usuario : usuarioPage.toList()) {
				usuario.add(linkTo(methodOn(UsuarioController.class).buscar(usuario.getCdUsuario())).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(usuarioPage);
	}

	@GetMapping("/{cdUsuario}")
	public ResponseEntity<Object> buscar(@PathVariable(value = "cdUsuario") UUID cdUsuario) {
		log.debug("Buscar usuário: {}", cdUsuario);
		Optional<Usuario> usuarioOptional = usuarioService.buscar(cdUsuario);
		if (!usuarioOptional.isPresent()) {
			log.debug("Usuário não encontrado: {}", cdUsuario);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(usuarioOptional.get());
		}
	}

	@PostMapping("/cadastro")
	public ResponseEntity<Object> adicionar(
			@RequestBody @Validated(UsuarioDto.UsuarioView.UsuarioPost.class) @JsonView(UsuarioDto.UsuarioView.UsuarioPost.class) UsuarioDto usuarioDto) {
		log.debug("Adicionar usuário: {} - {}", usuarioDto.getLogin(), usuarioDto.getEmail());
		if (usuarioService.existsByLogin(usuarioDto.getLogin())) {
			log.debug("Esse login já existe: {}", usuarioDto.getLogin());
			return ResponseEntity.status(HttpStatus.OK).body("Esse login já existe.");
		} else if (usuarioService.existsByEmail(usuarioDto.getEmail())) {
			log.debug("Esse email já existe: {}", usuarioDto.getEmail());
			return ResponseEntity.status(HttpStatus.OK).body("Esse email já existe.");
		} else {
			var usuario = new Usuario();
			BeanUtils.copyProperties(usuarioDto, usuario);
			usuario.setStatusUsuario(StatusUsuario.ATIVO);
			usuario.setDtCadastro(LocalDateTime.now(ZoneId.of("UTC")));
			usuario = usuarioService.salvarUsuario(usuario);
			log.debug("Usuário adicionado com sucesso: {}", usuario.getCdUsuario());
			return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
		}
	}

	@PutMapping("/{cdUsuario}")
	public ResponseEntity<Object> atualizar(@PathVariable(value = "cdUsuario") UUID cdUsuario,
			@RequestBody @Validated(UsuarioDto.UsuarioView.UsuarioPut.class) @JsonView(UsuarioDto.UsuarioView.UsuarioPut.class) UsuarioDto usuarioDto) {
		log.debug("Atualizar usuário: {}", cdUsuario);
		Optional<Usuario> usuarioOptional = usuarioService.buscar(cdUsuario);
		if (!usuarioOptional.isPresent()) {
			log.debug("Usuário não encontrado: {}", cdUsuario);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
		} else if (!usuarioDto.getEmail().equals(usuarioOptional.get().getEmail())
				&& usuarioService.existsByEmail(usuarioDto.getEmail())) {
			log.debug("Esse email já é utilizado: {}", usuarioDto.getEmail());
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Esse email já é utilizado.");
		} else {
			var usuario = usuarioOptional.get();
			usuario.setNome(usuarioDto.getNome());
			usuario.setDtAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));
			usuario = usuarioService.salvarUsuario(usuario);
			log.debug("Usuário atualizado com sucesso: {}", cdUsuario);
			return ResponseEntity.status(HttpStatus.OK).body(usuario);
		}
	}

	@PutMapping("/{cdUsuario}/senha")
	public ResponseEntity<Object> atualizarSenha(@PathVariable(value = "cdUsuario") UUID cdUsuario,
			@RequestBody @Validated(UsuarioDto.UsuarioView.SenhaPut.class) @JsonView(UsuarioDto.UsuarioView.SenhaPut.class) UsuarioDto usuarioDto) {
		log.debug("Atualizar senha do usuário: {}", cdUsuario);
		Optional<Usuario> usuarioOptional = usuarioService.buscar(cdUsuario);
		if (!usuarioOptional.isPresent()) {
			log.debug("Usuário não encontrado: {}", cdUsuario);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
		} else if (!usuarioOptional.get().getSenha().equals(usuarioDto.getSenha())) {
			log.debug("A senha atual está incorreta: {}", cdUsuario);
			return ResponseEntity.status(HttpStatus.CONFLICT).body("A senha atual está incorreta.");
		} else {
			var usuario = usuarioOptional.get();
			usuario.setSenha(usuarioDto.getNovaSenha());
			usuario.setDtAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));
			usuarioService.atualizarSenha(usuario);
			log.debug("A senha foi atualizada com sucesso: {}", usuario.getCdUsuario());
			return ResponseEntity.status(HttpStatus.OK).body("A senha foi atualizada com sucesso.");
		}
	}

	@DeleteMapping("/{cdUsuario}")
	public ResponseEntity<Object> excluir(@PathVariable(value = "cdUsuario") UUID cdUsuario) {
		log.debug("Excluir usuário: {}", cdUsuario);
		Optional<Usuario> usuarioOptional = usuarioService.buscar(cdUsuario);
		if (!usuarioOptional.isPresent()) {
			log.debug("Usuário não encontrado: {}", cdUsuario);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
		} else {
			usuarioService.excluir(usuarioOptional.get());
			log.debug("Usuário excluído com sucesso: {}", cdUsuario);
			return ResponseEntity.status(HttpStatus.OK).body("Usuário excluído com sucesso.");
		}
	}

	@PostMapping("/{cdUsuario}/papeis")
	public ResponseEntity<Object> vincular(@PathVariable(value = "cdUsuario") UUID cdUsuario,
			@RequestBody @Validated(PapelUsuarioDto.PapelUsuarioView.PapelUsuarioPost.class) @JsonView(PapelUsuarioDto.PapelUsuarioView.PapelUsuarioPost.class) PapelUsuarioDto papelUsuarioDto) {
		log.debug("Vincular papel ao usuário: {} - {}", papelUsuarioDto.getCdPapel(), cdUsuario);
		if (papelUsuarioService.existsByCdPapelAndCdUsuario(papelUsuarioDto.getCdPapel(), cdUsuario)) {
			log.debug("Esse papel já está vinculado a esse usuário: {}", papelUsuarioDto.getCdPapel(), cdUsuario);
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Esse papel já está vinculado a esse usuário.");
		}
		Optional<Papel> papelOptional = papelService.buscar(papelUsuarioDto.getCdPapel());
		if (!papelOptional.isPresent()) {
			log.debug("Papel não encontrado: {}", papelUsuarioDto.getCdPapel());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Papel não encontrado.");
		}
		Optional<Usuario> usuarioOptional = usuarioService.buscar(cdUsuario);
		if (!usuarioOptional.isPresent()) {
			log.debug("Usuário não encontrado: {}", papelUsuarioDto.getCdPapel());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
		} else {
			var papelUsuario = new PapelUsuario();
			papelUsuario.setFlAtivo(Boolean.TRUE);
			papelUsuario.setDtCadastro(LocalDateTime.now(ZoneId.of("UTC")));
			papelUsuario.setPapel(papelOptional.get());
			papelUsuario.setUsuario(usuarioOptional.get());
			papelUsuarioService.adicionar(papelUsuario);
			log.debug("Papel vinculado ao usuário com sucesso: {} - {}", papelUsuarioDto.getCdPapel(), cdUsuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(papelUsuario);
		}
	}

	@PutMapping("/{cdUsuario}/papeis")
	public ResponseEntity<Object> atualizarVinculo(@PathVariable(value = "cdUsuario") UUID cdUsuario,
			@RequestBody @Validated(PapelUsuarioDto.PapelUsuarioView.PapelUsuarioPut.class) @JsonView(PapelUsuarioDto.PapelUsuarioView.PapelUsuarioPut.class) PapelUsuarioDto papelUsuarioDto) {
		log.debug("Atualizar vínculo do papel com usuário: {} - {}", papelUsuarioDto.getCdPapel(), cdUsuario);
		Optional<PapelUsuario> papelUsuarioOptional = papelUsuarioService.buscar(papelUsuarioDto.getCdPapel(),
				cdUsuario);
		if (!papelUsuarioOptional.isPresent()) {
			log.debug("Esse papel não está vinculado a esse usuário: {}", papelUsuarioDto.getCdPapel(), cdUsuario);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esse papel não está vinculado a esse usuário.");
		}
		Optional<Papel> papelOptional = papelService.buscar(papelUsuarioDto.getCdPapel());
		if (!papelOptional.isPresent()) {
			log.debug("Papel não encontrado: {}", papelUsuarioDto.getCdPapel());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Papel não encontrado.");
		}
		Optional<Usuario> usuarioOptional = usuarioService.buscar(cdUsuario);
		if (!usuarioOptional.isPresent()) {
			log.debug("Usuário não encontrado: {}", papelUsuarioDto.getCdPapel());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
		} else {
			var papelUsuario = papelUsuarioOptional.get();
			papelUsuario.setFlAtivo(papelUsuarioDto.getFlAtivo());
			papelUsuario.setDtAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));
			papelUsuarioService.atualizar(papelUsuario);
			log.debug("Vinculo de papel com usuário foi atualizado com sucesso: {} - {}", papelUsuarioDto.getCdPapel(),
					cdUsuario);
			return ResponseEntity.status(HttpStatus.OK).body(papelUsuario);
		}
	}

	@DeleteMapping("/{cdUsuario}/papeis/{cdPapel}")
	public ResponseEntity<Object> excluirVinculo(@PathVariable(value = "cdUsuario") UUID cdUsuario,
			@PathVariable(value = "cdPapel") UUID cdPapel) {
		log.debug("Excluir vínculo do papel com usuário: {} - {}", cdPapel, cdUsuario);
		Optional<PapelUsuario> papelUsuarioOptional = papelUsuarioService.buscar(cdPapel, cdUsuario);
		if (!papelUsuarioOptional.isPresent()) {
			log.debug("Esse papel não está vinculado a esse usuário: {}", cdPapel, cdUsuario);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esse papel não está vinculado a esse usuário.");
		}
		Optional<Papel> papelOptional = papelService.buscar(cdPapel);
		if (!papelOptional.isPresent()) {
			log.debug("Papel não encontrado: {}", cdPapel);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Papel não encontrado.");
		}
		Optional<Usuario> usuarioOptional = usuarioService.buscar(cdUsuario);
		if (!usuarioOptional.isPresent()) {
			log.debug("Usuário não encontrado: {}", cdPapel);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
		} else {
			papelUsuarioService.excluir(cdPapel, cdUsuario);
			log.debug("Vinculo de papel com usuário foi excluído com sucesso: {} - {}", cdPapel, cdUsuario);
			return ResponseEntity.status(HttpStatus.OK).body("Vinculo de papel com usuário foi excluído com sucesso.");
		}
	}

	@GetMapping("/{cdUsuario}/papeis/{cdPapel}")
	public ResponseEntity<Object> buscarVinculo(@PathVariable(value = "cdUsuario") UUID cdUsuario,
			@PathVariable(value = "cdPapel") UUID cdPapel) {
		log.debug("Buscar vínculo do papel com usuário: {} - {}", cdPapel, cdUsuario);
		Optional<PapelUsuario> papelUsuarioOptional = papelUsuarioService.buscar(cdPapel, cdUsuario);
		if (!papelUsuarioOptional.isPresent()) {
			log.debug("Esse papel não está vinculado a esse usuário: {}", cdPapel, cdUsuario);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esse papel não está vinculado a esse usuário.");
		} else {
			log.debug("Vínculo de papel com usuário foi encontrado com sucesso: {} - {}", cdPapel, cdUsuario);
			return ResponseEntity.status(HttpStatus.OK).body(papelUsuarioOptional.get());
		}
	}

}
