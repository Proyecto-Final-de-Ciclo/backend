package com.example.demo.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.JwtResponseDto;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.MessageResponse;
import com.example.demo.dto.SignupDto;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.utils.SanitizerUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UsuarioRepository usuarioRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getNombre(), loginDto.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String rol = userDetails.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ERROR");

    return ResponseEntity.ok(new JwtResponseDto(jwt, "Bearer",
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        rol));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDto signUpRequest) {
    String nombreSanitizado = SanitizerUtil.sanitize(signUpRequest.getNombre(), "usuario");
    if (usuarioRepository.existsByNombre(nombreSanitizado)) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Ya existe un usuario con ese nombre"));
    }

    if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Ya existe un usuario con ese email"));
    }

    // Create new user's account
    Usuario user = new Usuario();
    user.setNombre(nombreSanitizado);
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    user.setEmail(signUpRequest.getEmail());
    user.setFechaRegistro(LocalDate.now());
    user.setRol(Rol.USER);

    usuarioRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
    String error = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(e -> e.getDefaultMessage())
        .orElse("Error de validación");
    return ResponseEntity.badRequest().body(new MessageResponse(error));
  }
}
