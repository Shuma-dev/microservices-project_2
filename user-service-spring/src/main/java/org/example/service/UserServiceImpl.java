package org.example.service;

import org.example.entity.User;
import org.example.event.Operation;
import org.example.event.UserEvent;
import org.example.exception.UserNotFoundException;
import org.example.producer.UserEventProducer;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public UserServiceImpl(UserRepository userRepository,
                           UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Неверный Id");
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public User createUser(String name, String email, Integer age) {
        UserValidator.validate(name, email, age);
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже существует");
        }
        User user = new User(name, email, age);
        User savedUser = userRepository.save(user);
        UserEvent event = new UserEvent(
                Operation.CREATE,
                savedUser.getEmail()
        );
        userEventProducer.send(event);
        return savedUser;
    }

    @Override
    public User findById(Long id) {
        validateId(id);
        return getUserById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(Long id, String name, String email, Integer age) {
        validateId(id);
        User user = getUserById(id);
        UserValidator.validate(name, email, age);
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже существует");
        }
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        validateId(id);
        User user = getUserById(id);
        userRepository.deleteById(id);
        UserEvent event = new UserEvent(
                Operation.DELETE,
                user.getEmail()
        );
        userEventProducer.send(event);
    }
}
