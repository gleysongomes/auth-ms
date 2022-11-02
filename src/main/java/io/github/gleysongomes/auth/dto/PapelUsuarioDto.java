package io.github.gleysongomes.auth.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PapelUsuarioDto {

	@NotNull(groups = { PapelUsuarioView.PapelUsuarioPost.class, PapelUsuarioView.PapelUsuarioPut.class })
	@JsonView({ PapelUsuarioView.PapelUsuarioPost.class, PapelUsuarioView.PapelUsuarioPut.class })
	private UUID cdPapel;

	@NotNull(groups = PapelUsuarioView.PapelUsuarioPut.class)
	@JsonView(PapelUsuarioView.PapelUsuarioPut.class)
	private Boolean flAtivo;

	public interface PapelUsuarioView {
		public static interface PapelUsuarioPost {
		}

		public static interface PapelUsuarioPut {
		}
	}

}
