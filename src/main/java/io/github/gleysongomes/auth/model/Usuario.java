package io.github.gleysongomes.auth.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.gleysongomes.auth.enums.StatusUsuario;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tb_usuario")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Usuario extends RepresentationModel<Usuario> implements Serializable {

	private static final long serialVersionUID = 4832702997834169432L;

	@Id
	@Column(name = "cd_usuario")
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID cdUsuario;

	@Column(nullable = false, unique = true, length = 60)
	private String login;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(nullable = false, length = 100)
	private String nome;

	@JsonIgnore
	@Column(nullable = false, length = 60)
	private String senha;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@Column(name = "dt_cadastro", nullable = false)
	private LocalDateTime dtCadastro;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@Column(name = "dt_atualizacao")
	private LocalDateTime dtAtualizacao;

	@Column(name = "status_usuario", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private StatusUsuario statusUsuario;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_papel_usuario", joinColumns = @JoinColumn(name = "cd_usuario"), inverseJoinColumns = @JoinColumn(name = "cd_papel"))
	private Set<Papel> papeis = new HashSet<>();
}
