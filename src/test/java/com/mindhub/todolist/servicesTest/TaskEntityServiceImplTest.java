package com.mindhub.todolist.servicesTest;

import com.mindhub.todolist.dtos.TaskEntityDTO.*;
import com.mindhub.todolist.enums.Status;
import com.mindhub.todolist.mappers.TaskEntityMapper;
import com.mindhub.todolist.models.TaskEntity;
import com.mindhub.todolist.repositories.ITaskEntityRepository;
import com.mindhub.todolist.repositories.IUserEntityRepository;
import com.mindhub.todolist.services.impl.TaskEntityServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskEntityServiceImplTest {

    @Mock
    private ITaskEntityRepository taskEntityRepository;

    @Mock
    private IUserEntityRepository userEntityRepository;

    @Mock
    private TaskEntityMapper taskEntityMapper;

    @InjectMocks
    private TaskEntityServiceImpl taskEntityService;

    private TaskEntity mockTask;
    private TaskEntityRequestDTO mockRequestDTO;
    private TaskEntityResponseDTO mockResponseDTO;
    private TaskEntityUpdateDTO mockUpdateDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        mockTask = new TaskEntity();
        mockTask.setId(1L);
        mockTask.setTitle("Comprar Pan");
        mockTask.setDescription("Ir a comprar pan");
        mockTask.setStatus(Status.PENDING);

        mockRequestDTO = new TaskEntityRequestDTO(
                "Comprar Pan",
                "Ir a comprar pan",
                Status.PENDING,  // Ahora usando el enum Status
                1L
        );

        mockResponseDTO = new TaskEntityResponseDTO(
                1L,
                "Comprar Pan",
                "Ir a comprar pan",
                Status.PENDING
        );

        mockUpdateDTO = new TaskEntityUpdateDTO(
                1L,
                "Comprar Azucar",
                "Ir a comprar azucar",
                Status.COMPLETED
        );
    }

    @Test
    void countByUserEntityIdShouldReturnCount() {
        // Arrange
        Long userId = 1L;
        when(taskEntityRepository.countByUserEntityId(userId)).thenReturn(5L);

        // Act
        Long result = taskEntityService.countByUserEntityId(userId);

        // Assert
        assertEquals(5L, result);
        verify(taskEntityRepository).countByUserEntityId(userId);
    }

    @Test
    void saveTaskShouldReturnTaskResponseDTO() {
        // Arrange
        when(taskEntityMapper.toEntity(mockRequestDTO)).thenReturn(mockTask);
        when(taskEntityRepository.save(any(TaskEntity.class))).thenReturn(mockTask);
        when(taskEntityMapper.toTaskResponseDTO(mockTask)).thenReturn(mockResponseDTO);

        // Act
        TaskEntityResponseDTO result = taskEntityService.saveTask(mockRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponseDTO.id(), result.id());
        assertEquals(mockResponseDTO.title(), result.title());
        verify(taskEntityRepository).save(any(TaskEntity.class));
    }

    @Test
    void getAllShouldReturnAllTasks() {
        // Arrange
        when(taskEntityRepository.findAll()).thenReturn(Arrays.asList(mockTask));
        Set<TaskEntityResponseDTO> expectedResponse = new HashSet<>(Arrays.asList(mockResponseDTO));
        when(taskEntityMapper.toTaskResponseSetDTO(any())).thenReturn(expectedResponse);

        // Act
        Set<TaskEntityResponseDTO> result = taskEntityService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskEntityRepository).findAll();
    }

    @Test
    void updateShouldUpdateAndReturnTask() {
        // Arrange
        when(taskEntityRepository.findById(mockUpdateDTO.id())).thenReturn(Optional.of(mockTask));
        when(taskEntityRepository.save(any(TaskEntity.class))).thenReturn(mockTask);
        when(taskEntityMapper.toTaskResponseDTO(mockTask)).thenReturn(mockResponseDTO);

        // Act
        TaskEntityResponseDTO result = taskEntityService.update(mockUpdateDTO);

        // Assert
        assertNotNull(result);
        verify(taskEntityRepository).save(any(TaskEntity.class));
    }

    @Test
    void updateShouldThrowEntityNotFoundException() {
        // Arrange
        when(taskEntityRepository.findById(mockUpdateDTO.id())).thenReturn(Optional.empty());

        // Act & Assert
        //assertThrows(EntityNotFoundException.class, () -> taskEntityService.update(mockUpdateDTO));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> taskEntityService.update(mockUpdateDTO));
        assertEquals("El ID de la tarea no fue encontrada", exception.getMessage());
        verify(taskEntityRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void deleteShouldDeleteTask() {
        // Arrange
        when(taskEntityRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        // Act
        taskEntityService.delete(1L);

        // Assert
        verify(taskEntityRepository).delete(mockTask);
    }

    @Test
    void deleteShouldThrowEntityNotFoundException() {
        // Arrange
        when(taskEntityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskEntityService.delete(1L));
    }
}