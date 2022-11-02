package io.github.gleysongomes.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.gleysongomes.auth.model.PapelUsuario;
import io.github.gleysongomes.auth.model.primarykey.PapelUsuarioId;

public interface PapelUsuarioRepository extends JpaRepository<PapelUsuario, PapelUsuarioId> {

	@Query(value = "select case when count(*) > 0 then true else false end from PapelUsuario pu where pu.papel.cdPapel = :cdPapel and pu.usuario.cdUsuario = :cdUsuario")
	boolean existsByCdPapelAndCdUsuario(@Param("cdPapel") UUID cdPapel, @Param("cdUsuario") UUID cdUsuario);

	@Modifying
	@Query(value = "delete from PapelUsuario pu where pu.papel.cdPapel = :cdPapel and pu.usuario.cdUsuario = :cdUsuario")
	void excluir(@Param("cdPapel") UUID cdPapel, @Param("cdUsuario") UUID cdUsuario);

	@Query(value = "select pu from PapelUsuario pu where pu.papel.cdPapel = :cdPapel and pu.usuario.cdUsuario = :cdUsuario")
	Optional<PapelUsuario> buscar(@Param("cdPapel") UUID cdPapel, @Param("cdUsuario") UUID cdUsuario);

	@Modifying
	@Query(value = "update PapelUsuario pu set flAtivo = :#{#papelUsuario.flAtivo}, dtAtualizacao = :#{#papelUsuario.dtAtualizacao} where pu.papel.cdPapel = :#{#papelUsuario.papel.cdPapel} and pu.usuario.cdUsuario = :#{#papelUsuario.usuario.cdUsuario}")
	void atualizar(@Param("papelUsuario") PapelUsuario papelUsuario);

	@Modifying
	@Query(value = "insert into tb_papel_usuario (cd_papel, cd_usuario, dt_cadastro, fl_ativo) values (:#{#papelUsuario.papel.cdPapel}, :#{#papelUsuario.usuario.cdUsuario}, :#{#papelUsuario.dtCadastro}, :#{#papelUsuario.flAtivo})", nativeQuery = true)
	void adicionar(@Param("papelUsuario") PapelUsuario papelUsuario);
}
