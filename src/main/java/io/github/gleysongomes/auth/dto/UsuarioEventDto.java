package io.github.gleysongomes.auth.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioEventDto {

	private UUID cdUsuario;

	private String login;

	private String email;

	private String nome;

	private String statusUsuario;

	private String tipoAcao;
}
