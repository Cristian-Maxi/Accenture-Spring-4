package com.mindhub.todolist.servicesTest;

import com.mindhub.todolist.dtos.AdminEntityDTO.AdminEntityRequestDTO;
import com.mindhub.todolist.dtos.AdminEntityDTO.AdminEntityResponseDTO;
import com.mindhub.todolist.dtos.AdminEntityDTO.AdminEntityUpdateDTO;
import com.mindhub.todolist.dtos.UserEntityDTO.DatosAutenticacionUsuario;
import com.mindhub.todolist.exceptions.ApplicationException;
import com.mindhub.todolist.mappers.AdminEntityMapper;
import com.mindhub.todolist.models.AdminEntity;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.IAdminEntityRepository;
import com.mindhub.todolist.services.impl.AdminEntityServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mindhub.todolist.repositories.IUserEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminEntityServiceImplTest {

    @Mock
    private IAdminEntityRepository adminEntityRepository;

    @Mock
    private IUserEntityRepository userEntityRepository;

    @Mock
    private AdminEntityMapper adminEntityMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminEntityServiceImpl adminEntityService;

    private AdminEntity adminEntity;
    private AdminEntityRequestDTO adminEntityRequestDTO;
    private AdminEntityResponseDTO adminEntityResponseDTO;
    private AdminEntityUpdateDTO adminEntityUpdateDTO;

    @BeforeEach
    void setUp() {

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("cristian@outlook.com");
        userEntity.setPassword("password123");

        adminEntity = new AdminEntity();
        adminEntity.setId(1L);
        adminEntity.setName("Cristian");
        adminEntity.setLastname("Gomez");
        adminEntity.setUser(userEntity);

        DatosAutenticacionUsuario datosAutenticacionUsuario = new DatosAutenticacionUsuario(
                "cristian@outlook.com",
                "password123"
        );

        adminEntityRequestDTO = new AdminEntityRequestDTO(
                "Cristian",
                "Gomez",
                datosAutenticacionUsuario
        );

        adminEntityResponseDTO = new AdminEntityResponseDTO(
                1L,
                "Cristian",
                "Gomez"
        );

        adminEntityUpdateDTO = new AdminEntityUpdateDTO(
                1L,
                "Maximiliano",
                "Montenegro"
        );
    }

    @Test
    void testSavedAdminEntity_Success() {
        when(userEntityRepository.existsByEmail(adminEntityRequestDTO.user().getEmail())).thenReturn(false);
        when(passwordEncoder.encode(adminEntityRequestDTO.user().getPassword())).thenReturn("encodedPassword");
        when(adminEntityMapper.toEntity(adminEntityRequestDTO)).thenReturn(adminEntity);
        when(adminEntityRepository.save(any(AdminEntity.class))).thenReturn(adminEntity);
        when(adminEntityMapper.toAdminResponseDTO(adminEntity)).thenReturn(adminEntityResponseDTO);

        AdminEntityResponseDTO result = adminEntityService.savedAdminEntity(adminEntityRequestDTO);

        assertNotNull(result);
        assertEquals("Cristian", result.name());
        assertEquals("Gomez", result.lastname());

        verify(adminEntityRepository).save(adminEntity);
    }

    @Test
    void testSavedAdminEntity_EmailAlreadyExists() {
        when(userEntityRepository.existsByEmail(adminEntityRequestDTO.user().getEmail())).thenReturn(true);

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                adminEntityService.savedAdminEntity(adminEntityRequestDTO)
        );

        assertEquals("El email ya existe en la base de datos", exception.getMessage());
        verify(adminEntityRepository, never()).save(any(AdminEntity.class));
    }

    @Test
    void testUpdate_Success() {
        when(adminEntityRepository.findById(adminEntityUpdateDTO.id())).thenReturn(Optional.of(adminEntity));
        when(adminEntityRepository.save(adminEntity)).thenReturn(adminEntity);
        when(adminEntityMapper.toAdminResponseDTO(any(AdminEntity.class))).thenReturn(
                new AdminEntityResponseDTO(1L, "UpdatedName", "UpdatedLastname")
        );

        AdminEntityResponseDTO result = adminEntityService.update(adminEntityUpdateDTO);

        assertNotNull(result);
        assertEquals("Maximiliano", result.name());
        assertEquals("Montenegro", result.lastname());

        verify(adminEntityRepository).save(adminEntity);
    }

    @Test
    void testUpdate_EntityNotFound() {
        when(adminEntityRepository.findById(adminEntityUpdateDTO.id())).thenReturn(Optional.empty());

        //assertThrows(EntityNotFoundException.class, () -> adminEntityService.update(adminEntityUpdateDTO));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                adminEntityService.update(adminEntityUpdateDTO)
        );

        assertEquals("El ID del usuario no encontrado", exception.getMessage());
        verify(adminEntityRepository, never()).save(any(AdminEntity.class));
    }

    @Test
    void testDelete_Success() {
        when(adminEntityRepository.findById(adminEntity.getId())).thenReturn(Optional.of(adminEntity));

        adminEntityService.delete(adminEntity.getId());

        verify(adminEntityRepository).delete(adminEntity);
    }

    @Test
    void testDelete_EntityNotFound() {
        when(adminEntityRepository.findById(adminEntity.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                adminEntityService.delete(adminEntity.getId())
        );

        assertEquals("No se encontró el ID del usuario ingresado", exception.getMessage());
        verify(adminEntityRepository, never()).delete(any(AdminEntity.class));
    }

    @Test
    void testExistById_Success() {
        when(adminEntityRepository.existsById(adminEntity.getId())).thenReturn(true);

        boolean exists = adminEntityService.existById(adminEntity.getId());

        assertTrue(exists);
        verify(adminEntityRepository).existsById(adminEntity.getId());
    }

    @Test
    void testExistById_NotExists() {
        when(adminEntityRepository.existsById(adminEntity.getId())).thenReturn(false);

        boolean exists = adminEntityService.existById(adminEntity.getId());

        assertFalse(exists);
        verify(adminEntityRepository).existsById(adminEntity.getId());
    }
}
