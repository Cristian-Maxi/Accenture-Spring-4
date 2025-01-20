package com.mindhub.todolist.repositoriesTest;

import com.mindhub.todolist.enums.RoleEnum;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.IUserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ExtendWith(SpringExtension.class)
public class UserEntityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IUserEntityRepository userEntityRepository;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setEmail("cristian@outlook.com");
        userEntity.setPassword("password123");
        userEntity.setRol(RoleEnum.USER);
    }

    @Test
    void testFindById_Success() {
        UserEntity savedUser = entityManager.persistFlushFind(userEntity);

        Optional<UserEntity> result = userEntityRepository.findById(savedUser.getId());

        assertTrue(result.isPresent());
        assertEquals(savedUser.getId(), result.get().getId());
        assertEquals(savedUser.getEmail(), result.get().getEmail());
    }

    @Test
    void testFindById_NotFound() {
        Optional<UserEntity> result = userEntityRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testSave_Success() {
        UserEntity savedUser = userEntityRepository.save(userEntity);

        assertNotNull(savedUser.getId());
        assertEquals(userEntity.getEmail(), savedUser.getEmail());
    }

    @Test
    void testFindAll_Success() {
        entityManager.persist(userEntity);

        List<UserEntity> users = userEntityRepository.findAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals(userEntity.getEmail(), users.get(0).getEmail());
    }

    @Test
    void testExistsByEmail_Success() {
        entityManager.persist(userEntity);

        boolean exists = userEntityRepository.existsByEmail("cristian@outlook.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_NotExists() {
        boolean exists = userEntityRepository.existsByEmail("cristian@outlook.com");

        assertFalse(exists);
    }

    @Test
    void testDeleteById_Success() {
        UserEntity savedUser = entityManager.persistFlushFind(userEntity);

        userEntityRepository.deleteById(savedUser.getId());
        entityManager.flush();

        Optional<UserEntity> result = userEntityRepository.findById(savedUser.getId());
        assertFalse(result.isPresent());
    }
}
