package com.Ristretto.API.domain.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserRegistrationData data) {
        // Criptografa a senha
        String hashedPassword = passwordEncoder.encode(data.password());

        // Cria UserRegistrationData com senha criptografada
        var dataWithHashedPassword = new UserRegistrationData(
                data.name(),
                data.email(),
                hashedPassword,
                data.theme(),
                data.defaultReminderMin(),
                data.avatarUrl()
        );

        // Cria e salva o usuário
        User user = new User(dataWithHashedPassword);
        return user;
    }

    public User findById(Long id) {
        return repository.getReferenceById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public User save(@Valid User user) {
        return repository.save(user);
    }

}