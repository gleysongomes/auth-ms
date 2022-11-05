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
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.github.gleysongomes.auth.dto.PapelDto;
import io.github.gleysongomes.auth.model.Papel;
import io.github.gleysongomes.auth.service.PapelService;
import io.github.gleysongomes.auth.specification.SpecificationTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/papeis", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class PapelController {

	@Autowired
	private PapelService papelService;

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping
	public ResponseEntity<Page<Papel>> listar(SpecificationTemplate.PapelSpec papelSpec,
			@PageableDefault(page = 0, size = 10, sort = "dtCadastro", direction = Sort.Direction.DESC) Pageable pageable) {
		log.debug("Listar papeis utilizado filtros e paginação: {} - {}", papelSpec, pageable);
		Page<Papel> papelPage = papelService.listar(papelSpec, pageable);
		if (!papelPage.isEmpty()) {
			for (Papel papel : papelPage.toList()) {
				papel.add(linkTo(methodOn(PapelController.class).buscar(papel.getCdPapel())).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(papelPage);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/{cdPapel}")
	public ResponseEntity<Object> buscar(@PathVariable(value = "cdPapel") UUID cdPapel) {
		log.debug("Buscar papel: {}", cdPapel);
		Optional<Papel> papelOptional = papelService.buscar(cdPapel);
		if (!papelOptional.isPresent()) {
			log.debug("Papel não encontrado: {}", cdPapel);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Papel não encontrado.");
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(papelOptional.get());
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping
	public ResponseEntity<Object> adicionar(
			@RequestBody @Validated(PapelDto.PapelView.PapelPost.class) @JsonView(PapelDto.PapelView.PapelPost.class) PapelDto papelDto) {
		log.debug("Adicionar papel: {} - {}", papelDto);
		if (papelService.existsByNome(papelDto.getNome())) {
			log.debug("Esse nome já existe: {}", papelDto.getNome());
			return ResponseEntity.status(HttpStatus.OK).body("Esse nome já existe.");
		} else {
			var papel = new Papel();
			BeanUtils.copyProperties(papelDto, papel);
			papel.setFlAtivo(Boolean.TRUE);
			papel.setDtCadastro(LocalDateTime.now(ZoneId.of("UTC")));
			papel = papelService.salvar(papel);
			log.debug("Papel adicionado com sucesso: {}", papel.getCdPapel());
			return ResponseEntity.status(HttpStatus.CREATED).body(papel);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("/{cdPapel}")
	public ResponseEntity<Object> atualizar(@PathVariable(value = "cdPapel") UUID cdPapel,
			@RequestBody @Validated(PapelDto.PapelView.PapelPut.class) @JsonView(PapelDto.PapelView.PapelPut.class) PapelDto papelDto) {
		log.debug("Atualizar papel: {}", cdPapel);
		Optional<Papel> papelOptional = papelService.buscar(cdPapel);
		if (!papelOptional.isPresent()) {
			log.debug("Papel não encontrado: {}", cdPapel);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Papel não encontrado.");
		} else if (!papelDto.getNome().equals(papelOptional.get().getNome())
				&& papelService.existsByNome(papelDto.getNome())) {
			log.debug("Esse nome já existe: {}", papelDto.getNome());
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Esse nome já existe.");
		} else {
			var papel = papelOptional.get();
			papel.setNome(papelDto.getNome());
			papel.setFlAtivo(papelDto.getFlAtivo());
			papel.setDtAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));
			papelService.atualizar(papel);
			log.debug("Papel atualizado com sucesso: {}", cdPapel);
			return ResponseEntity.status(HttpStatus.OK).body(papel);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping("/{cdPapel}")
	public ResponseEntity<Object> excluir(@PathVariable(value = "cdPapel") UUID cdPapel) {
		log.debug("Excluir papel: {}", cdPapel);
		if (!papelService.existsByCdPapel(cdPapel)) {
			log.debug("Papel não encontrado: {}", cdPapel);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Papel não encontrado.");
		} else {
			papelService.excluir(cdPapel);
			log.debug("Papel excluído com sucesso: {}", cdPapel);
			return ResponseEntity.status(HttpStatus.OK).body("Papel excluído com sucesso.");
		}
	}

}
