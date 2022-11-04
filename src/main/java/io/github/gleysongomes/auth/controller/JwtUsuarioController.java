package io.github.gleysongomes.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.gleysongomes.auth.dto.JwtUsuarioDto;
import io.github.gleysongomes.auth.service.JwtUsuarioService;

@RestController
@RequestMapping(path = "/jwts", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class JwtUsuarioController {

	@Autowired
	private JwtUsuarioService jwtUsuarioService;

	@GetMapping("/{login}")
	public ResponseEntity<List<JwtUsuarioDto>> listarPorLogin(@PathVariable(value = "login") String login) {
		return ResponseEntity.status(HttpStatus.OK).body(jwtUsuarioService.listarPorLogin(login));
	}

	@DeleteMapping("/{login}")
	public ResponseEntity<Object> excluir(@PathVariable(value = "login") String login) {
		if (!jwtUsuarioService.existsByLogin(login)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existem jwts para esse login.");
		}
		jwtUsuarioService.excluir(login);
		return ResponseEntity.status(HttpStatus.OK).body("Jwts excluídos com sucesso.");
	}

}
