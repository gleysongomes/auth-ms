package io.github.gleysongomes.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PapelDto {

	@Size(max = 100, groups = { PapelView.PapelPost.class, PapelView.PapelPut.class })
	@NotBlank(groups = { PapelView.PapelPost.class, PapelView.PapelPut.class })
	@JsonView({ PapelView.PapelPost.class, PapelView.PapelPut.class })
	private String nome;

	@NotNull(groups = PapelView.PapelPut.class)
	@JsonView(PapelView.PapelPut.class)
	private Boolean flAtivo;

	public interface PapelView {
		public static interface PapelPost {
		}

		public static interface PapelPut {
		}
	}

}
