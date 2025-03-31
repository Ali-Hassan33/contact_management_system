package com.contact_management_system.services;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.valueOf;

@Service
public class AuthService {

    private final UserService userService;
    private final JWTService jwtService;

    public AuthService(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public User signUp(UserDto user) {
        return userService.save(user);
    }

    public String login(Authentication authentication) throws JOSEException {
        final User user;

        var oAuth2Token = Optional.of(authentication)
                .filter(OAuth2AuthenticationToken.class::isInstance)
                .map(OAuth2AuthenticationToken.class::cast);

        if (oAuth2Token.isPresent())
            user = getOAuth2User(oAuth2Token.get().getPrincipal());
        else user = getBasicAuthUser(authentication);

        return jwtService.serializedJwt(user);
    }

    private User getBasicAuthUser(Authentication authentication) {
        return userService.getUserByEmail(valueOf(authentication.getPrincipal()));
    }

    private User getOAuth2User(OAuth2User oAuth2User) {
        var email = oAuth2User.<String>getAttribute("email");
        if (!userService.isUserExist(email)) {
            return userService.saveOAuth2User(
                    UserDto.builder()
                            .name(oAuth2User.getAttribute("name"))
                            .email(email)
                            .build()
            );
        }
        return userService.getUserByEmail(email);
    }
}
