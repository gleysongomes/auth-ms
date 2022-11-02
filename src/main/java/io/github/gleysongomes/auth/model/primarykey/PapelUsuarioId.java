package io.github.gleysongomes.auth.model.primarykey;

import java.io.Serializable;

import io.github.gleysongomes.auth.model.Papel;
import io.github.gleysongomes.auth.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PapelUsuarioId implements Serializable {

	private static final long serialVersionUID = 1344110287256233286L;

	private Papel papel;

	private Usuario usuario;
}
