package com.contact_management_system.services;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.contact_management_system.exceptions.EmailNotFoundException;
import com.contact_management_system.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public User save(UserDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        return save(new User(userDto.getName(), userDto.getEmail(), encodedPassword));
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public User saveOAuth2User(UserDto userDto) {
        return save(new User(userDto.getName(), userDto.getEmail()));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
    }

    public boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }

    private User save(User user) {
        return userRepository.save(user);
    }
}
