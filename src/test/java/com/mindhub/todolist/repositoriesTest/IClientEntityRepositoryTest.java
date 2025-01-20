package com.mindhub.todolist.repositoriesTest;

import com.mindhub.todolist.models.ClientEntity;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.IClientEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class IClientEntityRepositoryTest {

    @Autowired
    private IClientEntityRepository clientEntityRepository;

    @Test
    void testSaveClientEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");

        ClientEntity client = new ClientEntity("Cristian", "Gomez", user);
        ClientEntity savedClient = clientEntityRepository.save(client);

        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient.getName()).isEqualTo("Cristian");
        assertThat(savedClient.getLastname()).isEqualTo("Gomez");
        assertThat(savedClient.getUser().getEmail()).isEqualTo("cristian@outlook.com");
    }

    @Test
    void testFindById() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");

        ClientEntity client = new ClientEntity("Cristian", "Gomez", user);
        ClientEntity savedClient = clientEntityRepository.save(client);

        ClientEntity foundClient = clientEntityRepository.findById(savedClient.getId()).orElse(null);

        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getId()).isEqualTo(savedClient.getId());
    }

    @Test
    void testUpdateClientEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");

        ClientEntity client = new ClientEntity("Cristian", "Gomez", user);
        ClientEntity savedClient = clientEntityRepository.save(client);

        savedClient.setName("Maximiliano");
        savedClient.setLastname("Montenegro");
        ClientEntity updatedClient = clientEntityRepository.save(savedClient);

        assertThat(updatedClient.getName()).isEqualTo("Maximiliano");
        assertThat(updatedClient.getLastname()).isEqualTo("Montenegro");
    }

    @Test
    void testDeleteClientEntity() {
        UserEntity user = new UserEntity();
        user.setEmail("cristian@outlook.com");
        user.setPassword("password");;

        ClientEntity client = new ClientEntity("Cristian", "Gomez", user);
        ClientEntity savedClient = clientEntityRepository.save(client);

        clientEntityRepository.deleteById(savedClient.getId());
        ClientEntity deletedClient = clientEntityRepository.findById(savedClient.getId()).orElse(null);

        assertThat(deletedClient).isNull();
    }
}
