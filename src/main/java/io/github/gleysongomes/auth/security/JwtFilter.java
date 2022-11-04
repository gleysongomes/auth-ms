package io.github.gleysongomes.auth.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import io.github.gleysongomes.auth.service.JwtUsuarioService;

public class JwtFilter extends GenericFilterBean {

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private JwtUsuarioService jwtUsuarioService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String jwt = jwtProvider.getJwtHeader(req);
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
