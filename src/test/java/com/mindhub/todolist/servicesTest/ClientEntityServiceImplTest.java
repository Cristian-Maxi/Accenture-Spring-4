package com.mindhub.todolist.servicesTest;

import com.mindhub.todolist.dtos.ClientEntityDTO.ClientEntityRequestDTO;
import com.mindhub.todolist.dtos.ClientEntityDTO.ClientEntityResponseDTO;
import com.mindhub.todolist.dtos.ClientEntityDTO.ClientEntityUpdateDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.DatosAutenticacionUsuario;
import com.mindhub.todolist.exceptions.ApplicationException;
import com.mindhub.todolist.mappers.ClientEntityMapper;
import com.mindhub.todolist.models.ClientEntity;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.IClientEntityRepository;
import com.mindhub.todolist.repositories.IUserEntityRepository;
import com.mindhub.todolist.services.impl.ClientEntityServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientEntityServiceImplTest {

    @Mock
    private IClientEntityRepository clientEntityRepository;

    @Mock
    private IUserEntityRepository userEntityRepository;

    @Mock
    private ClientEntityMapper clientEntityMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientEntityServiceImpl clientEntityService;

    private ClientEntity clientEntity;
    private ClientEntityRequestDTO clientEntityRequestDTO;
    private ClientEntityResponseDTO clientEntityResponseDTO;
    private ClientEntityUpdateDTO clientEntityUpdateDTO;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("cristian@outlook.com");
        userEntity.setPassword("password123");

        clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        clientEntity.setName("John");
        clientEntity.setLastname("Doe");
        clientEntity.setUser(userEntity);

        DatosAutenticacionUsuario datosAutenticacionUsuario = new DatosAutenticacionUsuario(
                "cristian@outlook.com",
                "password123"
        );

        clientEntityRequestDTO = new ClientEntityRequestDTO(
                "Cristian",
                "Gomez",
                datosAutenticacionUsuario
        );

        clientEntityResponseDTO = new ClientEntityResponseDTO(
                1L,
                "Cristian",
                "Gomez"
        );

        clientEntityUpdateDTO = new ClientEntityUpdateDTO(
                1L,
                "Maximiliano",
                "Montenegro"
        );
    }

    @Test
    void testSaveClientEntitySuccess() {
        when(userEntityRepository.existsByEmail(clientEntityRequestDTO.user().getEmail())).thenReturn(false);
        when(passwordEncoder.encode(clientEntityRequestDTO.user().getPassword())).thenReturn("encodedPassword");
        when(clientEntityMapper.toEntity(clientEntityRequestDTO)).thenReturn(clientEntity);
        when(clientEntityRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientEntityMapper.toClientResponseDTO(clientEntity)).thenReturn(clientEntityResponseDTO);

        ClientEntityResponseDTO result = clientEntityService.saveClientEntity(clientEntityRequestDTO);

        assertNotNull(result);
        assertEquals(clientEntity.getName(), result.name());
        assertEquals(clientEntity.getLastname(), result.lastname());

        verify(clientEntityRepository).save(clientEntity);
    }

    @Test
    void testSaveClientEntityEmailAlreadyExists() {
        when(userEntityRepository.existsByEmail(clientEntityRequestDTO.user().getEmail())).thenReturn(true);

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                clientEntityService.saveClientEntity(clientEntityRequestDTO)
        );

        assertEquals("El email ya existe en la base de datos", exception.getMessage());
        verify(clientEntityRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    void testUpdateSuccess() {
        when(clientEntityRepository.findById(clientEntityUpdateDTO.id())).thenReturn(Optional.of(clientEntity));
        when(clientEntityMapper.toClientResponseDTO(clientEntity)).thenReturn(
                new ClientEntityResponseDTO(1L, "UpdatedName", "UpdatedLastname")
        );

        ClientEntityResponseDTO result = clientEntityService.update(clientEntityUpdateDTO);

        assertNotNull(result);
        assertEquals(clientEntity.getName(), result.name());
        assertEquals(clientEntity.getLastname(), result.lastname());

        verify(clientEntityRepository).save(clientEntity);
    }

    @Test
    void testUpdateEntityNotFound() {
        when(clientEntityRepository.findById(clientEntityUpdateDTO.id())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                clientEntityService.update(clientEntityUpdateDTO)
        );

        assertEquals("El ID del usuario no encontrado", exception.getMessage());
        verify(clientEntityRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    void testDeleteSuccess() {
        when(clientEntityRepository.findById(clientEntity.getId())).thenReturn(Optional.of(clientEntity));

        clientEntityService.delete(clientEntity.getId());

        verify(clientEntityRepository).delete(clientEntity);
    }

    @Test
    void testDeleteEntityNotFound() {
        when(clientEntityRepository.findById(clientEntity.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                clientEntityService.delete(clientEntity.getId())
        );

        assertEquals("No se encontró el ID del usuario ingresado", exception.getMessage());
        verify(clientEntityRepository, never()).delete(any(ClientEntity.class));
    }

    @Test
    void testExistByIdSuccess() {
        when(clientEntityRepository.existsById(clientEntity.getId())).thenReturn(true);

        boolean exists = clientEntityService.existById(clientEntity.getId());

        assertTrue(exists);
        verify(clientEntityRepository).existsById(clientEntity.getId());
    }

    @Test
    void testExistByIdNotExists() {
        when(clientEntityRepository.existsById(clientEntity.getId())).thenReturn(false);

        boolean exists = clientEntityService.existById(clientEntity.getId());

        assertFalse(exists);
        verify(clientEntityRepository).existsById(clientEntity.getId());
    }
}