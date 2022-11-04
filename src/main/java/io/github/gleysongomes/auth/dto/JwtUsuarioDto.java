package io.github.gleysongomes.auth.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class JwtUsuarioDto implements Serializable {

	private static final long serialVersionUID = -1447974481229192677L;

	private UUID cdJwtUsuario;

	@NonNull
	private String jwt;

	@NonNull
	private String login;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private LocalDateTime dtCadastro;
}
