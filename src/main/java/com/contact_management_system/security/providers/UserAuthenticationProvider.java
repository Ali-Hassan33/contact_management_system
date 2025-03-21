package com.contact_management_system.security.providers;

import com.contact_management_system.entities.User;
import com.contact_management_system.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.valueOf;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        userRepository.findByEmail(valueOf(authentication.getPrincipal()))
                .map(User::getPassword)
                .filter(password -> passwordEncoder.matches(rawPassword(authentication), password))
                .orElseThrow(() -> new BadCredentialsException("Invalid Password."));

        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), List.of());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

    private static String rawPassword(Authentication authentication) {
        return authentication.getCredentials().toString();
    }
}
