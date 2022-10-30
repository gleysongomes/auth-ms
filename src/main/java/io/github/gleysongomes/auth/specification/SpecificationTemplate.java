package io.github.gleysongomes.auth.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.gleysongomes.auth.model.Usuario;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

public class SpecificationTemplate {

	@And({
		@Spec(path = "login", spec = Like.class),
		@Spec(path = "email", spec = Like.class),
		@Spec(path = "nome", spec = Like.class),
		@Spec(path = "statusUsuario", spec = Equal.class)
	})
	public interface UsuarioSpec extends Specification<Usuario> {
	}

}
