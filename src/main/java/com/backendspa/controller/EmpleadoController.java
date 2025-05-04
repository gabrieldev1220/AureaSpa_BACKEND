package com.backendspa.controller;

import com.backendspa.entity.Empleado;
import com.backendspa.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        return ResponseEntity.ok(empleadoService.getEmpleadosForReservas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'RECEPCIONISTA', 'GERENTE_GENERAL')")
    public ResponseEntity<?> getEmpleadoById(@PathVariable Long id) {
        Optional<Empleado> empleado = empleadoService.getEmpleadoById(id);
        if (empleado.isPresent()) {
            return ResponseEntity.ok(empleado.get());
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Empleado no encontrado");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('GERENTE_GENERAL')")
    public ResponseEntity<?> createEmpleado(@RequestBody Empleado empleado) {
        try {
            Empleado nuevoEmpleado = empleadoService.createEmpleado(empleado);
            return ResponseEntity.ok(nuevoEmpleado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el empleado: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE_GENERAL')")
    public ResponseEntity<?> updateEmpleado(@PathVariable Long id, @RequestBody Empleado empleadoDetails) {
        try {
            Empleado updatedEmpleado = empleadoService.updateEmpleado(id, empleadoDetails);
            return ResponseEntity.ok(updatedEmpleado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el empleado: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE_GENERAL')")
    public ResponseEntity<?> deleteEmpleado(@PathVariable Long id) {
        try {
            empleadoService.deleteEmpleado(id);
            return ResponseEntity.ok("Empleado eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar el empleado: " + e.getMessage());
        }
    }
}