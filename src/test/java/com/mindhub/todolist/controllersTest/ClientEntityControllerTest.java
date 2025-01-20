package com.mindhub.todolist.controllersTest;

import com.mindhub.todolist.controllers.ClientEntityController;
import com.mindhub.todolist.dtos.ApiResponseDTO;
import com.mindhub.todolist.dtos.ClientEntityDTO.ClientEntityRequestDTO;
import com.mindhub.todolist.dtos.ClientEntityDTO.ClientEntityResponseDTO;
import com.mindhub.todolist.dtos.ClientEntityDTO.ClientEntityUpdateDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.DatosAutenticacionUsuario;
import com.mindhub.todolist.services.IClientEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClientEntityControllerTest {

    @InjectMocks
    private ClientEntityController clientEntityController;

    @Mock
    private IClientEntityService clientEntityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateClientEntity() {
        ClientEntityRequestDTO requestDTO = new ClientEntityRequestDTO(
                "Cristian",
                "Gomez",
                new DatosAutenticacionUsuario("cristian@outlook.com", "password123")
        );

        ClientEntityResponseDTO responseDTO = new ClientEntityResponseDTO(
                1L,
                "Cristian",
                "Gomez"
        );

        when(clientEntityService.saveClientEntity(any(ClientEntityRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ClientEntityResponseDTO> response = clientEntityController.createClientEntity(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    public void testUpdateClientEntity() {
        ClientEntityUpdateDTO updateDTO = new ClientEntityUpdateDTO(
                1L,
                "Maximiliano",
                "Montenegro"
        );

        ClientEntityResponseDTO responseDTO = new ClientEntityResponseDTO(
                1L,
                "Maximiliano",
                "Montenegro"
        );

        when(clientEntityService.update(any(ClientEntityUpdateDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ApiResponseDTO<ClientEntityResponseDTO>> response = clientEntityController.updateClientEntity(updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cliente Actualizado", response.getBody().getMessage());
        assertEquals(responseDTO, response.getBody().getData());
    }

    @Test
    public void testDeleteEntity() {
        Long id = 1L;
        doNothing().when(clientEntityService).delete(id);

        ResponseEntity<?> response = clientEntityController.deleteEntity(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cliente Eliminado exitosamente", response.getBody());
    }

    @Test
    public void testExistClientEntity() {
        Long id = 1L;
        when(clientEntityService.existById(id)).thenReturn(true);

        ResponseEntity<?> response = clientEntityController.existClientEntity(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("El cliente existe en la base de datos", response.getBody());
    }

    @Test
    public void testClientEntityDoesNotExist() {
        Long id = 1L;
        when(clientEntityService.existById(id)).thenReturn(false);

        ResponseEntity<?> response = clientEntityController.existClientEntity(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("El cliente no existe en la base de datos", response.getBody());
    }
}
