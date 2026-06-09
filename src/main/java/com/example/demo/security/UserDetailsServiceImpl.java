package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Usuario;
import com.example.demo.repositories.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UsuarioRepository usuarioRepository;

  // crea un userdetailsimpl desde un nombre de usuario
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByNombre(nombre);
    if (usuario == null)
      throw new UsernameNotFoundException("Usuario no encontrado: " + nombre);
    return UserDetailsImpl.build(usuario);
  }

}
