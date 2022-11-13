package io.github.gleysongomes.auth.security;

import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTParser;

import io.github.gleysongomes.auth.dto.LoginDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	@Autowired
	private AuthenticationManager authenticationManager;

	private PublicKey publicKey;

	private PublicKey publicKeyFile;

	private PrivateKey privateKey;

	@Value("${app.auth.senha-jwt}")
	private String senhaJwt;

	@Value("${app.auth.expiracao-jwt-minutos}")
	private Integer expiracaoJwtMinutos;

	@Value("${app.auth.key-alias}")
	private String keyAlias;

	@Value("${app.auth.key-store}")
	private String keyStore;

	@Value("${app.auth.key-store-password}")
	private String keyStorePassword;

	@PostConstruct
	public void init() {
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(keyStore),
				keyStorePassword.toCharArray());
		KeyPair kp = keyStoreKeyFactory.getKeyPair(keyAlias);
		publicKey = kp.getPublic();
		publicKeyFile = getPublicKeyFile();
		privateKey = kp.getPrivate();
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
				.claim("public_key_file", "N")
				.setIssuedAt(new Date())
				.setExpiration(Date.from(Instant.now().plus(expiracaoJwtMinutos, ChronoUnit.MINUTES)))
				.signWith(privateKey, SignatureAlgorithm.RS512).compact();
	}

	public Authentication criarAutenticacao(String jwt) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(jwt)
				.getBody();
		List<GrantedAuthority> papeis = Arrays.asList(claims.get("papeis").toString().split(","))
				.stream()
				.map(papel -> new SimpleGrantedAuthority(papel))
				.collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", papeis);
	}

	public boolean valido(String jwt) {
		try {
			Jwts.parserBuilder().setSigningKey(getPublicKey(jwt)).build().parseClaimsJws(jwt);
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

	public UUID getJwtId(String jwt) {
		String id = Jwts.parserBuilder().setSigningKey(getPublicKey(jwt)).build().parseClaimsJws(jwt).getBody().getId();
		return UUID.fromString(id);
	}

	private PublicKey getPublicKeyFile() {
		try {
			Resource resource = new ClassPathResource("auth-jwt.public");
			byte[] keyBytes = Files.readAllBytes(resource.getFile().toPath());
			byte[] encoded = Base64.getDecoder().decode(new String(keyBytes));
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (Exception e) {
			log.debug("Erro ao ler chave pública: {}", e);
			return null;
		}
	}

	private Key getPublicKey(String jwt) {
		try {
			Map<String, Object> claims = JWTParser.parse(jwt).getJWTClaimsSet().getClaims();
			Object flPublicKeyFile = claims.get("public_key_file");
			if ("S".equals(flPublicKeyFile)) {
				return publicKeyFile;
			}
			return publicKey;
		} catch (Exception e) {
			log.debug("Erro ao ler claim do token: {}", e);
			return null;
		}
	}

}
