package com.mindhub.todolist.repositoriesTest;

import com.mindhub.todolist.enums.Status;
import com.mindhub.todolist.models.TaskEntity;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.ITaskEntityRepository;
import com.mindhub.todolist.repositories.IUserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ITaskEntityRepositoryTest {

    @Autowired
    private ITaskEntityRepository taskEntityRepository;

    @Autowired
    private IUserEntityRepository userEntityRepository;

    @Test
    void testSaveTaskEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Comprar Pan");
        task.setDescription("Ir a comprar pan");
        task.setStatus(Status.PENDING);
        task.setUserEntity(savedUser);

        TaskEntity savedTask = taskEntityRepository.save(task);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Comprar Pan");
        assertThat(savedTask.getUserEntity().getEmail()).isEqualTo("cristian@outlook.com");
    }

    @Test
    void testFindById() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Comprar Pan");
        task.setDescription("Ir a comprar pan");
        task.setStatus(Status.PENDING);
        task.setUserEntity(savedUser);
        TaskEntity savedTask = taskEntityRepository.save(task);

        TaskEntity foundTask = taskEntityRepository.findById(savedTask.getId()).orElse(null);

        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(savedTask.getId());
    }

    @Test
    void testUpdateTaskEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Comprar Pan");
        task.setDescription("Ir a comprar pan");
        task.setStatus(Status.PENDING);
        task.setUserEntity(savedUser);
        TaskEntity savedTask = taskEntityRepository.save(task);

        savedTask.setTitle("Pan Comprado");
        savedTask.setStatus(Status.COMPLETED);
        TaskEntity updatedTask = taskEntityRepository.save(savedTask);

        assertThat(updatedTask.getTitle()).isEqualTo("Pan Comprado");
        assertThat(updatedTask.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    void testDeleteTaskEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Comprar Pan");
        task.setDescription("Ir a comprar pan");
        task.setStatus(Status.PENDING);
        task.setUserEntity(savedUser);
        TaskEntity savedTask = taskEntityRepository.save(task);

        taskEntityRepository.deleteById(savedTask.getId());
        TaskEntity deletedTask = taskEntityRepository.findById(savedTask.getId()).orElse(null);

        assertThat(deletedTask).isNull();
    }

    @Test
    void testCountByUserEntityId() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task1 = new TaskEntity();
        task1.setTitle("Comprar Pan");
        task1.setDescription("Ir a comprar pan");
        task1.setStatus(Status.PENDING);
        task1.setUserEntity(savedUser);
        taskEntityRepository.save(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setTitle("Comprar Azucar");
        task2.setDescription("Ir a comprar azucar");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setUserEntity(savedUser);
        taskEntityRepository.save(task2);

        Long taskCount = taskEntityRepository.countByUserEntityId(savedUser.getId());

        assertThat(taskCount).isEqualTo(2);
    }
}