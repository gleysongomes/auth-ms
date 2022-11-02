package io.github.gleysongomes.auth.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gleysongomes.auth.enums.StatusUsuario;
import io.github.gleysongomes.auth.model.Usuario;
import io.github.gleysongomes.auth.service.UsuarioService;

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioService usuarioService;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> usuarioOptional = usuarioService.findByLogin(username);
		if (!usuarioOptional.isPresent()) {
			throw new UsernameNotFoundException("Usuário não encontrado.");
		}
		if (usuarioOptional.get().getStatusUsuario().equals(StatusUsuario.BLOQUEADO)) {
			throw new RuntimeException("Usuário bloqueado.");
		}
		List<GrantedAuthority> papeis = usuarioOptional.get()
				.getPapeis()
				.stream()
				.map(papel -> new SimpleGrantedAuthority(papel.getNome()))
				.collect(Collectors.toList());
		return new User(usuarioOptional.get().getLogin(), usuarioOptional.get().getSenha(), papeis);
	}

}
