package com.mindhub.todolist.controllersTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mindhub.todolist.controllers.TaskEntityController;
import com.mindhub.todolist.dtos.ApiResponseDTO;
import com.mindhub.todolist.dtos.TaskEntityDTO.TaskEntityRequestDTO;
import com.mindhub.todolist.dtos.TaskEntityDTO.TaskEntityResponseDTO;
import com.mindhub.todolist.dtos.TaskEntityDTO.TaskEntityUpdateDTO;
import com.mindhub.todolist.enums.Status;
import com.mindhub.todolist.services.ITaskEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

public class TaskEntityControllerTest {

    @InjectMocks
    private TaskEntityController taskEntityController;

    @Mock
    private ITaskEntityService taskEntityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTask() {
        TaskEntityRequestDTO requestDto = new TaskEntityRequestDTO(
                "Comprar Pan",
                "Ir a comprar pan",
                Status.PENDING,
                1L
        );

        TaskEntityResponseDTO responseDto = new TaskEntityResponseDTO(
                1L,
                "Comprar Pan",
                "Ir a comprar pan",
                Status.PENDING
        );

        when(taskEntityService.saveTask(any(TaskEntityRequestDTO.class))).thenReturn(responseDto);

        ResponseEntity<TaskEntityResponseDTO> response = taskEntityController.createTask(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    public void testGetAllUsuarios() {
        Set<TaskEntityResponseDTO> taskEntities = new HashSet<>();
        when(taskEntityService.getAll()).thenReturn(taskEntities);

        ResponseEntity<ApiResponseDTO<TaskEntityResponseDTO>> response = taskEntityController.getAllUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No Hay Tareas Disponibles", response.getBody().getMessage());
    }

    @Test
    public void testUpdateTask() {
        TaskEntityUpdateDTO updateDto = new TaskEntityUpdateDTO(
                1L,
                "Comprar Azucar",
                "Ir a comprar azucar",
                Status.PENDING
        );

        TaskEntityResponseDTO responseDto = new TaskEntityResponseDTO(
                1L,
                "Comprar Azucar",
                "Ir a comprar azucar",
                Status.PENDING
        );
        when(taskEntityService.update(any(TaskEntityUpdateDTO.class))).thenReturn(responseDto);

        ResponseEntity<ApiResponseDTO<TaskEntityUpdateDTO>> response = taskEntityController.updateTask(updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tarea Actualizada", response.getBody().getMessage());
    }

    @Test
    public void testDeleteTask() {
        Long id = 1L;
        doNothing().when(taskEntityService).delete(id);

        ResponseEntity<?> response = taskEntityController.deleteTask(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tarea Eliminada exitosamente", response.getBody());
    }

    @Test
    public void testUserTasksCount() {
        Long id = 1L;
        Long taskCount = 5L;
        when(taskEntityService.countByUserEntityId(id)).thenReturn(taskCount);

        ResponseEntity<?> response = taskEntityController.userTasksCount(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El usuario con ID " + id + " tiene " + taskCount + " tareas.", ((ApiResponseDTO<?>) response.getBody()).getMessage());
    }
}
