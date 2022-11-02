package io.github.gleysongomes.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

	@NotBlank
	private String login;

	@NotBlank
	private String senha;
}
