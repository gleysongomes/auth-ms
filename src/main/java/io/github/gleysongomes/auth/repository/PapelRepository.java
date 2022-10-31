package io.github.gleysongomes.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.gleysongomes.auth.model.Papel;

public interface PapelRepository extends JpaRepository<Papel, UUID>, JpaSpecificationExecutor<Papel> {

	@Query(value = "select case when count(*) > 0 then true else false end from tb_papel p where p.cd_papel = :cdPapel", nativeQuery = true)
	boolean existsByCdPapel(@Param("cdPapel") UUID cdPapel);

	@Modifying
	@Query(value = "delete from tb_papel p where p.cd_papel = :cdPapel", nativeQuery = true)
	void excluir(@Param("cdPapel") UUID cdPapel);

	@Query(value = "select * from tb_papel p where p.cd_papel = :cdPapel", nativeQuery = true)
	Optional<Papel> buscar(@Param("cdPapel") UUID cdPapel);

	@Query(value = "select case when count(*) > 0 then true else false end from tb_papel p where p.nome = :nome", nativeQuery = true)
	boolean existsByNome(@Param("nome") String nome);

	@Modifying
	@Query(value = "update tb_papel p set nome = :#{#papel.nome}, fl_ativo = :#{#papel.flAtivo} where p.cd_papel = :#{#papel.cdPapel}", nativeQuery = true)
	void atualizar(@Param("papel") Papel papel);
}
