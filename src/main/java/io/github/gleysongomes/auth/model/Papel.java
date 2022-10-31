package io.github.gleysongomes.auth.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tb_papel")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Papel extends RepresentationModel<Papel> implements Serializable {

	private static final long serialVersionUID = 7479886536672853608L;

	@Id
	@Column(name = "cd_papel")
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID cdPapel;

	@Column(nullable = false, length = 100, unique = true)
	private String nome;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@Column(name = "dt_cadastro", nullable = false)
	private LocalDateTime dtCadastro;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@Column(name = "dt_atualizacao")
	private LocalDateTime dtAtualizacao;

	@Column(name = "fl_ativo", nullable = false)
	private Boolean flAtivo;
}
