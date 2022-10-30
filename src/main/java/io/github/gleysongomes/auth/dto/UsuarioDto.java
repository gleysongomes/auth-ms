package io.github.gleysongomes.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDto {

	@Size(min = 4, max = 60, groups = UsuarioView.UsuarioPost.class)
	@NotBlank(groups = UsuarioView.UsuarioPost.class)
	@JsonView(UsuarioView.UsuarioPost.class)
	private String login;

	@Size(max = 254, groups = { UsuarioView.UsuarioPost.class, UsuarioView.UsuarioPut.class })
	@NotBlank(groups = { UsuarioView.UsuarioPost.class, UsuarioView.UsuarioPut.class })
	@Email(groups = { UsuarioView.UsuarioPost.class, UsuarioView.UsuarioPut.class })
	@JsonView({ UsuarioView.UsuarioPost.class, UsuarioView.UsuarioPut.class })
	private String email;

	@Size(max = 100, groups = { UsuarioView.UsuarioPost.class, UsuarioView.UsuarioPut.class })
	@JsonView({ UsuarioView.UsuarioPost.class, UsuarioView.UsuarioPut.class })
	private String nome;

	@Size(min = 5, max = 60, groups = { UsuarioView.UsuarioPost.class, UsuarioView.SenhaPut.class })
	@NotBlank(groups = { UsuarioView.UsuarioPost.class, UsuarioView.SenhaPut.class })
	@JsonView({ UsuarioView.UsuarioPost.class, UsuarioView.SenhaPut.class })
	private String senha;

	@Size(min = 5, max = 60, groups = UsuarioView.SenhaPut.class)
	@NotBlank(groups = UsuarioView.SenhaPut.class)
	@JsonView(UsuarioView.SenhaPut.class)
	private String novaSenha;

	public interface UsuarioView {
		public static interface UsuarioPost {
		}

		public static interface UsuarioPut {
		}

		public static interface SenhaPut {
		}
	}

}
