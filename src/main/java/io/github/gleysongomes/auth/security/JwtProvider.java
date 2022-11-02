package io.github.gleysongomes.auth.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.github.gleysongomes.auth.dto.LoginDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	@Autowired
	private AuthenticationManager authenticationManager;

	private Key key;

	@Value("${app.auth.senha-jwt}")
	private String senhaJwt;

	@Value("${app.auth.expiracao-jwt-minutos}")
	private Integer expiracaoJwtMinutos;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(senhaJwt.getBytes());
	}

	public String gerar(LoginDto loginDto) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getSenha()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String papeis = authentication.getAuthorities()
				.stream()
				.map(papel -> papel.getAuthority())
				.collect(Collectors.joining(","));
		return Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setSubject(authentication.getName())
				.claim("papeis", papeis)
				.setIssuedAt(new Date())
				.setExpiration(Date.from(Instant.now().plus(expiracaoJwtMinutos, ChronoUnit.MINUTES)))
				.signWith(key, SignatureAlgorithm.HS512).compact();
	}

	public Authentication criarAutenticacao(String jwt) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(jwt)
				.getBody();
		List<GrantedAuthority> papeis = Arrays.asList(claims.get("papeis").toString().split(","))
				.stream()
				.map(papel -> new SimpleGrantedAuthority(papel))
				.collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", papeis);
	}

	public boolean validar(String jwt) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
			return true;
		} catch (Exception e) {
			log.debug("Erro ao validar token: {}", e);
			return false;
		}
	}

	public String getJwtHeader(HttpServletRequest req) {
		String auth = req.getHeader("Authorization");
		if (StringUtils.isNotBlank(auth) && auth.startsWith("Bearer ")) {
			return auth.substring(7);
		}
		return null;
	}

}
