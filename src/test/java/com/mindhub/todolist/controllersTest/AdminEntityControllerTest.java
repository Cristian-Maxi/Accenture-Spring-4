package com.mindhub.todolist.controllersTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mindhub.todolist.controllers.AdminEntityController;
import com.mindhub.todolist.dtos.AdminEntityDTO.AdminEntityRequestDTO;
import com.mindhub.todolist.dtos.AdminEntityDTO.AdminEntityResponseDTO;
import com.mindhub.todolist.dtos.AdminEntityDTO.AdminEntityUpdateDTO;
import com.mindhub.todolist.dtos.ApiResponseDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.DatosAutenticacionUsuario;
import com.mindhub.todolist.services.IAdminEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AdminEntityControllerTest {

    @InjectMocks
    private AdminEntityController adminEntityController;

    @Mock
    private IAdminEntityService adminEntityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAdminEntity() {
        DatosAutenticacionUsuario datosAutenticacionUsuario = new DatosAutenticacionUsuario(
                "john.doe@outlook.com",
                "password123"
        );
        AdminEntityRequestDTO requestDTO = new AdminEntityRequestDTO(
                "John",
                "Doe",
                datosAutenticacionUsuario);

        AdminEntityResponseDTO responseDTO = new AdminEntityResponseDTO(
                1L,
                "John",
                "Doe");

        when(adminEntityService.savedAdminEntity(any(AdminEntityRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<AdminEntityResponseDTO> response = adminEntityController.createAdminEntity(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    public void testUpdateAdminEntity() {
        AdminEntityUpdateDTO updateDTO = new AdminEntityUpdateDTO(
                1L,
                "newName",
                "newLastname");

        AdminEntityResponseDTO responseDTO = new AdminEntityResponseDTO(
                1L,
                "John",
                "Doe");

        when(adminEntityService.update(any(AdminEntityUpdateDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ApiResponseDTO<AdminEntityResponseDTO>> response = adminEntityController.updateAdminEntity(updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Admin Actualizado", response.getBody().getMessage());
    }

    @Test
    public void testDeleteEntity() {
        Long id = 1L;
        doNothing().when(adminEntityService).delete(id);

        ResponseEntity<?> response = adminEntityController.deleteEntity(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Admin Eliminado exitosamente", response.getBody());
    }

    @Test
    public void testExistAdminEntity() {
        Long id = 1L;
        when(adminEntityService.existById(id)).thenReturn(true);

        ResponseEntity<?> response = adminEntityController.existAdminEntity(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("El Admin existe en la base de datos", response.getBody());
    }
}
