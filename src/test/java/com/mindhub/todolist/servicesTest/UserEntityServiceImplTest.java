package com.mindhub.todolist.servicesTest;

import com.mindhub.todolist.dtos.TaskEntityDTO.TaskEntityResponseDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.UserEntityResposeDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.UserEntityTasksResponseDTO;
import com.mindhub.todolist.enums.RoleEnum;
import com.mindhub.todolist.enums.Status;
import com.mindhub.todolist.mappers.TaskEntityMapper;
import com.mindhub.todolist.mappers.UserEntityMapper;
import com.mindhub.todolist.models.TaskEntity;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.ITaskEntityRepository;
import com.mindhub.todolist.repositories.IUserEntityRepository;
import com.mindhub.todolist.services.impl.UserEntityServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceImplTest {

    @Mock
    private IUserEntityRepository userEntityRepository;

    @Mock
    private ITaskEntityRepository taskEntityRepository;

    @Mock
    private TaskEntityMapper taskEntityMapper;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserEntityServiceImpl userEntityService;

    private UserEntity userEntity;
    private TaskEntity taskEntity;
    private UserEntityResposeDTO userEntityResponseDTO;
    private UserEntityTasksResponseDTO userEntityTasksResponseDTO;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("cristian@outlook.com");
        userEntity.setRol(RoleEnum.ADMIN);

        taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setTitle("Task 1");
        taskEntity.setDescription("Description");
        taskEntity.setStatus(Status.PENDING);

        userEntityResponseDTO = new UserEntityResposeDTO(
                1L,
                "cristian@outlook.com",
                RoleEnum.ADMIN
        );

        userEntityTasksResponseDTO = new UserEntityTasksResponseDTO(
                1L,
                "cristian@outlook.com",
                RoleEnum.ADMIN,
                Set.of(new TaskEntityResponseDTO(1L, "Task 1", "Description", Status.PENDING))
        );
    }

    @Test
    void testGetAll_Success() {
        when(userEntityRepository.findAll()).thenReturn(List.of(userEntity));
        when(userEntityMapper.toResponseListDTO(List.of(userEntity))).thenReturn(List.of(userEntityResponseDTO));

        List<UserEntityResposeDTO> result = userEntityService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("cristian@outlook.com", result.get(0).email());
        assertEquals("ADMIN", result.get(0).rol().toString());

        verify(userEntityRepository).findAll();
        verify(userEntityMapper).toResponseListDTO(List.of(userEntity));
    }

    @Test
    void testUserTasks_Success() {
        when(userEntityRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(taskEntityRepository.findByUserEntityId(userEntity.getId())).thenReturn(List.of(taskEntity));
        when(taskEntityMapper.toTaskResponseSetDTO(List.of(taskEntity))).thenReturn(Set.of(new TaskEntityResponseDTO(1L, "Task 1", "Description", Status.PENDING)));

        UserEntityTasksResponseDTO result = userEntityService.userTasks(userEntity.getId());

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("cristian@outlook.com", result.username());
        assertEquals("ADMIN", result.rol().toString());
        assertEquals(1, result.taskEntitySet().size());

        verify(userEntityRepository).findById(userEntity.getId());
        verify(taskEntityRepository).findByUserEntityId(userEntity.getId());
        verify(taskEntityMapper).toTaskResponseSetDTO(List.of(taskEntity));
    }

    @Test
    void testUserTasks_UserNotFound() {
        when(userEntityRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userEntityService.userTasks(userEntity.getId())
        );

        assertEquals("No se encontró el ID del usuario ingresado", exception.getMessage());
        verify(userEntityRepository).findById(userEntity.getId());
        verify(taskEntityRepository, never()).findByUserEntityId(anyLong());
        verify(taskEntityMapper, never()).toTaskResponseSetDTO(any());
    }

    @Test
    void testUserTasks_NoTasksFound() {
        when(userEntityRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(taskEntityRepository.findByUserEntityId(userEntity.getId())).thenReturn(List.of());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userEntityService.userTasks(userEntity.getId())
        );

        assertEquals("No se encontraron tareas para el usuario con ID: " + userEntity.getId(), exception.getMessage());
        verify(userEntityRepository).findById(userEntity.getId());
        verify(taskEntityRepository).findByUserEntityId(userEntity.getId());
        verify(taskEntityMapper, never()).toTaskResponseSetDTO(any());
    }
}
