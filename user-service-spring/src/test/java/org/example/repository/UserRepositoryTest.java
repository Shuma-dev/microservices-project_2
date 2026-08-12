package org.example.repository;

import org.example.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("user_service")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        userRepository.deleteAll();
    }

    /* =====================
     save()
     =====================*/

    @Test
    @DisplayName("Пользователь создан")
    public void save_ShouldSaveUser() {
        User user = new User("Denis", "denis@gmail.com", 26);
        userRepository.save(user);
        assertNotNull(user.getId());
        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("Denis", savedUser.getName());
        assertEquals("denis@gmail.com", savedUser.getEmail());
        assertEquals(26, savedUser.getAge());
    }

    /* =====================
     findById()
     =====================*/

    @Test
    @DisplayName("Пользователь найден")
    public void findById_ShouldReturnUser() {
        User user = new User("Denis", "denis@gmail.com", 26);
        userRepository.save(user);
        assertNotNull(user.getId());
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        assertNotNull(foundUser);
        assertEquals("Denis", foundUser.getName());
        assertEquals("denis@gmail.com", foundUser.getEmail());
        assertEquals(26, foundUser.getAge());
    }

    @Test
    @DisplayName("Пользователь не найден")
    public void findById_ShouldReturnEmpty_WhenUserNotFound() {
        assertTrue(userRepository.findById(999L).isEmpty());
    }

    /* =====================
     findAll()
     =====================*/

    @Test
    @DisplayName("список пользователей")
    public void findAll_ShouldReturnUsers() {
        User user1 = new User("Denis", "denis@gmail.com", 26);
        User user2 = new User("Ivan", "ivan@gmail.com", 30);
        userRepository.save(user1);
        userRepository.save(user2);
        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Denis", users.get(0).getName());
        assertEquals("Ivan", users.get(1).getName());
    }

    @Test
    @DisplayName("список пользователей пуст")
    public void findAll_ShouldReturnEmptyList_WhenNoUsersExist() {
        List<User> users = userRepository.findAll();
        assertTrue(users.isEmpty());
    }

    /* =====================
     update()
     =====================*/

    @Test
    @DisplayName("пользователь обновлен")
    public void update_ShouldUpdateUser() {
        User user = new User("Denis", "denis@gmail.com", 26);
        userRepository.save(user);
        user.setName("Ivan");
        user.setEmail("ivan@gmail.com");
        user.setAge(30);
        userRepository.save(user);
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("Ivan", updatedUser.getName());
        assertEquals("ivan@gmail.com", updatedUser.getEmail());
        assertEquals(30, updatedUser.getAge());

    }

   /*  =====================
     delete()
     =====================*/

    @Test
    @DisplayName("пользователь удален")
    public void delete_ShouldDeleteUser() {
        User user = new User("Denis", "denis@gmail.com", 26);
        userRepository.save(user);
        userRepository.deleteById(user.getId());
        assertTrue(userRepository.findById(user.getId()).isEmpty());
    }
}
