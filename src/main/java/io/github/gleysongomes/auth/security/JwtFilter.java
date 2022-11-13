package io.github.gleysongomes.auth.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.gleysongomes.auth.service.JwtUsuarioService;

public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private JwtUsuarioService jwtUsuarioService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String jwt = jwtProvider.getJwtHeader(request);
		if (StringUtils.isNotBlank(jwt) && jwtProvider.valido(jwt) && naoRevogado(jwt)) {
			Authentication authentication = jwtProvider.criarAutenticacao(jwt);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(request, response);
	}

	private boolean naoRevogado(String jwt) {
		UUID cdJwtUsuario = jwtProvider.getJwtId(jwt);
		return jwtUsuarioService.existsByCdJwtUsuario(cdJwtUsuario);
	}

}
