package com.mindhub.todolist.controllersTest;

import com.mindhub.todolist.controllers.UserEntityController;
import com.mindhub.todolist.dtos.ApiResponseDTO;
import com.mindhub.todolist.dtos.TaskEntityDTO.TaskEntityResponseDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.UserEntityResposeDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.UserEntityTasksResponseDTO;
import com.mindhub.todolist.enums.RoleEnum;
import com.mindhub.todolist.enums.Status;
import com.mindhub.todolist.exceptions.ApplicationException;
import com.mindhub.todolist.services.IUserEntityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserEntityControllerTest {

    @InjectMocks
    private UserEntityController userEntityController;

    @Mock
    private IUserEntityService userEntityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsuarios_SuccessWithData() {
        List<UserEntityResposeDTO> userList = List.of(
                new UserEntityResposeDTO(1L, "cristian@outlook.com", RoleEnum.USER),
                new UserEntityResposeDTO(2L, "cristian@outlook.com", RoleEnum.ADMIN)
        );

        when(userEntityService.getAll()).thenReturn(userList);

        ResponseEntity<ApiResponseDTO<UserEntityResposeDTO>> response = userEntityController.getAllUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEstado());
        assertEquals("Usuarios Registrados", response.getBody().getMessage());
    }

    @Test
    public void testGetAllUsuarios_NoData() {
        when(userEntityService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponseDTO<UserEntityResposeDTO>> response = userEntityController.getAllUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEstado());
        assertEquals("No Hay Usuarios Registrados", response.getBody().getMessage());
    }

    @Test
    public void testGetAllUsuarios_Exception() {
        when(userEntityService.getAll()).thenThrow(new ApplicationException("Error fetching data"));

        Exception exception = assertThrows(ApplicationException.class, () -> {
            userEntityController.getAllUsuarios();
        });

        assertEquals(" Ha ocurrido un error Error fetching data", exception.getMessage());
    }

    @Test
    public void testFindUserTasksById_Success() {
        Long userId = 1L;
        UserEntityTasksResponseDTO userTasksResponse = new UserEntityTasksResponseDTO(
                userId,
                "cristian@outlook.com",
                RoleEnum.USER,
                Set.of(new TaskEntityResponseDTO(1L, "Task 1", "Task 1 description", Status.PENDING))
        );

        when(userEntityService.userTasks(userId)).thenReturn(userTasksResponse);

        ResponseEntity<ApiResponseDTO<UserEntityTasksResponseDTO>> response = userEntityController.findUserTasksById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEstado());
        assertEquals("Usuario y Tareas encontradas", response.getBody().getMessage());
        assertEquals(userTasksResponse, response.getBody().getData());
    }

    @Test
    public void testFindUserTasksById_UserNotFound() {
        Long userId = 1L;

        when(userEntityService.userTasks(userId)).thenThrow(new IllegalArgumentException("No se encontró el ID del usuario ingresado"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userEntityController.findUserTasksById(userId);
        });

        assertEquals("No se encontró el ID del usuario ingresado", exception.getMessage());
    }

    @Test
    public void testFindUserTasksById_NoTasksFound() {
        Long userId = 1L;

        when(userEntityService.userTasks(userId)).thenThrow(new EntityNotFoundException("No se encontraron tareas para el usuario con ID: " + userId));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            userEntityController.findUserTasksById(userId);
        });

        assertEquals("No se encontraron tareas para el usuario con ID: 1", exception.getMessage());
    }
}
