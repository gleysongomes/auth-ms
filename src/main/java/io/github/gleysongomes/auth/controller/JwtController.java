package io.github.gleysongomes.auth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.gleysongomes.auth.dto.JwtDto;
import io.github.gleysongomes.auth.dto.JwtUsuarioDto;
import io.github.gleysongomes.auth.dto.LoginDto;
import io.github.gleysongomes.auth.security.JwtProvider;
import io.github.gleysongomes.auth.service.JwtUsuarioService;

@RestController
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class JwtController {

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private JwtUsuarioService jwtUsuarioService;

	@PostMapping("/login")
	public ResponseEntity<JwtDto> login(@RequestBody @Valid LoginDto loginDto) {
		JwtDto jwtDto = new JwtDto(jwtProvider.gerar(loginDto));
		jwtUsuarioService.salvar(new JwtUsuarioDto(jwtDto.getToken(), loginDto.getLogin()));
		return ResponseEntity.ok(jwtDto);
	}

}
