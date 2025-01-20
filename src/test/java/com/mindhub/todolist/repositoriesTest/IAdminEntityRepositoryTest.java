package com.mindhub.todolist.repositoriesTest;
import com.mindhub.todolist.models.AdminEntity;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.IAdminEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class IAdminEntityRepositoryTest {

    @Autowired
    private IAdminEntityRepository adminEntityRepository;

    @Test
    void testSaveAdminEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");

        AdminEntity admin = new AdminEntity("Cristian", "Gomez", user);
        AdminEntity savedAdmin = adminEntityRepository.save(admin);

        assertThat(savedAdmin).isNotNull();
        assertThat(savedAdmin.getId()).isNotNull();
        assertThat(savedAdmin.getName()).isEqualTo("Cristian");
        assertThat(savedAdmin.getLastname()).isEqualTo("Gomez");
        assertThat(savedAdmin.getUser().getEmail()).isEqualTo("cristian@outlook.com");
    }

    @Test
    void testFindById() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");

        AdminEntity admin = new AdminEntity("Cristian", "Gomez", user);
        AdminEntity savedAdmin = adminEntityRepository.save(admin);

        AdminEntity foundAdmin = adminEntityRepository.findById(savedAdmin.getId()).orElse(null);

        assertThat(foundAdmin).isNotNull();
        assertThat(foundAdmin.getId()).isEqualTo(savedAdmin.getId());
    }

    @Test
    void testUpdateAdminEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");

        AdminEntity admin = new AdminEntity("Cristian", "Gomez", user);
        AdminEntity savedAdmin = adminEntityRepository.save(admin);

        savedAdmin.setName("Maximiiano");
        savedAdmin.setLastname("Montenegro");
        AdminEntity updatedAdmin = adminEntityRepository.save(savedAdmin);

        assertThat(updatedAdmin.getName()).isEqualTo("Maximiliano");
        assertThat(updatedAdmin.getLastname()).isEqualTo("Montenegro");
    }

    @Test
    void testDeleteAdminEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");

        AdminEntity admin = new AdminEntity("Cristian", "Gomez", user);
        AdminEntity savedAdmin = adminEntityRepository.save(admin);

        adminEntityRepository.deleteById(savedAdmin.getId());
        AdminEntity deletedAdmin = adminEntityRepository.findById(savedAdmin.getId()).orElse(null);

        assertThat(deletedAdmin).isNull();
    }
}
