package io.github.gleysongomes.auth.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.github.gleysongomes.auth.dto.JwtUsuarioDto;
import io.github.gleysongomes.auth.security.JwtProvider;
import io.github.gleysongomes.auth.service.JwtUsuarioService;

@Service
public class JwtUsuarioServiceImpl implements JwtUsuarioService {

	private static final String JWT_USUARIO_DTO = "JwtUsuarioDto";

	@Autowired
	private RedisTemplate<String, JwtUsuarioDto> redisTemplate;

	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public JwtUsuarioDto salvar(JwtUsuarioDto jwtUsuarioDto) {
		UUID cdJwtUsuario = jwtProvider.getJwtId(jwtUsuarioDto.getJwt());
		jwtUsuarioDto.setCdJwtUsuario(cdJwtUsuario);
		jwtUsuarioDto.setDtCadastro(LocalDateTime.now(ZoneId.of("UTC")));
		redisTemplate.opsForHash().put(JWT_USUARIO_DTO, cdJwtUsuario, jwtUsuarioDto);
		return jwtUsuarioDto;
	}

	@Override
	public void excluir(String login) {
		HashOperations<String, UUID, JwtUsuarioDto> hashOperations = redisTemplate.opsForHash();
		var jwtsUsuariosDtos = hashOperations.values(JWT_USUARIO_DTO);
		if (jwtsUsuariosDtos != null && !jwtsUsuariosDtos.isEmpty()) {
			var filtrados = jwtsUsuariosDtos.stream().filter(jwtUsuarioDto -> login.equals(jwtUsuarioDto.getLogin()))
					.collect(Collectors.toList());
			filtrados.forEach(jwt -> {
				hashOperations.delete(JWT_USUARIO_DTO, jwt.getCdJwtUsuario());
			});
		}
	}

	@Override
	public List<JwtUsuarioDto> listarPorLogin(String login) {
		HashOperations<String, UUID, JwtUsuarioDto> hashOperations = redisTemplate.opsForHash();
		var jwtsUsuariosDtos = hashOperations.values(JWT_USUARIO_DTO);
		if (jwtsUsuariosDtos != null && !jwtsUsuariosDtos.isEmpty()) {
			return jwtsUsuariosDtos.stream().filter(jwtUsuarioDto -> login.equals(jwtUsuarioDto.getLogin()))
					.collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public boolean existsByLogin(String login) {
		HashOperations<String, UUID, JwtUsuarioDto> hashOperations = redisTemplate.opsForHash();
		var jwtsUsuariosDtos = hashOperations.values(JWT_USUARIO_DTO);
		return jwtsUsuariosDtos != null && !jwtsUsuariosDtos.isEmpty();
	}

	@Override
	public boolean existsByCdJwtUsuario(UUID cdJwtUsuario) {
		HashOperations<String, UUID, JwtUsuarioDto> hashOperations = redisTemplate.opsForHash();
		var jwtUsuarioDto = hashOperations.get(JWT_USUARIO_DTO, cdJwtUsuario);
		return jwtUsuarioDto != null;
	}

}
