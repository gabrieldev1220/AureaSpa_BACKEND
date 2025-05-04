package com.backendspa.controller;

import com.backendspa.entity.Cliente;
import com.backendspa.entity.Empleado;
import com.backendspa.security.JwtUtil;
import com.backendspa.security.SpaUserDetails;
import com.backendspa.service.ClienteService;
import com.backendspa.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        SpaUserDetails userDetails = (SpaUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        // Log para depuración
        System.out.println("UserDetails: id=" + userDetails.getId() + ", userType=" + userDetails.getUserType());

        Map<String, String> response = new HashMap<>();
        response.put("jwt", jwt);
        response.put("userId", userDetails.getId().toString()); // Cambiar clienteId a userId
        if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
            String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            response.put("rol", role);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCliente(@RequestBody RegisterRequest registerRequest) {
        try {
            // Validar si el email ya existe
            if (clienteService.getClienteByEmail(registerRequest.getEmail()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "El email ya está registrado");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Crear un nuevo cliente
            Cliente cliente = new Cliente();
            cliente.setDni(registerRequest.getDni());
            cliente.setNombre(registerRequest.getNombre());
            cliente.setApellido(registerRequest.getApellido());
            cliente.setEmail(registerRequest.getEmail());
            cliente.setPassword(registerRequest.getPassword());
            cliente.setTelefono(registerRequest.getTelefono());

            // Guardar el cliente (la contraseña se encriptará en ClienteService)
            System.out.println("Guardando cliente: " + cliente.getEmail());
            Cliente clienteRegistrado = clienteService.createCliente(cliente);
            System.out.println("Cliente guardado exitosamente: " + clienteRegistrado.getEmail());

            // Devolver respuesta en formato JSON
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Cliente registrado exitosamente");
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error al registrar cliente: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Clases internas AuthRequest, AuthResponse, RegisterRequest
    static class AuthRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class AuthResponse {
        private final String jwt;

        public AuthResponse(String jwt) { this.jwt = jwt; }
        public String getJwt() { return jwt; }
    }

    static class RegisterRequest {
        private String dni;
        private String nombre;
        private String apellido;
        private String email;
        private String password;
        private String telefono;

        // Getters y setters
        public String getDni() { return dni; }
        public void setDni(String dni) { this.dni = dni; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
    }
}