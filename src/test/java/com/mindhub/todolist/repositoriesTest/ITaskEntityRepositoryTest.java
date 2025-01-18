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
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(Status.PENDING);
        task.setUserEntity(savedUser);

        TaskEntity savedTask = taskEntityRepository.save(task);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getUserEntity().getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    void testFindById() {
        UserEntity user = new UserEntity();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
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
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(Status.PENDING);
        task.setUserEntity(savedUser);
        TaskEntity savedTask = taskEntityRepository.save(task);

        savedTask.setTitle("Updated Task Title");
        savedTask.setStatus(Status.COMPLETED);
        TaskEntity updatedTask = taskEntityRepository.save(savedTask);

        assertThat(updatedTask.getTitle()).isEqualTo("Updated Task Title");
        assertThat(updatedTask.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    void testDeleteTaskEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task = new TaskEntity();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
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
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        UserEntity savedUser = userEntityRepository.save(user);

        TaskEntity task1 = new TaskEntity();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(Status.PENDING);
        task1.setUserEntity(savedUser);
        taskEntityRepository.save(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setUserEntity(savedUser);
        taskEntityRepository.save(task2);

        Long taskCount = taskEntityRepository.countByUserEntityId(savedUser.getId());

        assertThat(taskCount).isEqualTo(2);
    }
}