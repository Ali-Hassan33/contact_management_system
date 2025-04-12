package com.contact_management_system.services;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.entities.User;
import com.contact_management_system.enums.Label;
import com.contact_management_system.exceptions.EmailNotFoundException;
import com.contact_management_system.repositories.ContactProfileRepository;
import com.contact_management_system.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.contact_management_system.enums.Label.PERSONAL;
import static com.contact_management_system.enums.Label.WORK;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContactProfileRepository contactProfileRepository;
    private Long userId;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ContactProfileRepository contactProfileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.contactProfileRepository = contactProfileRepository;
    }

    public Page<ContactProfile> fetchContacts(Authentication authentication, Integer pageNumber, Integer pageSize) {
        this.userId = Optional.of(authentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(jwt -> jwt.getTokenAttributes().get("id"))
                .map(Long.class::cast)
                .orElseThrow(RuntimeException::new);
        if(pageNumber == null && pageSize == null)
            return contactProfileRepository.findAllByUserId(userId, Pageable.unpaged());
        return contactProfileRepository.findAllByUserId(userId, PageRequest.of(pageNumber, pageSize));
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

    private User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public ContactProfile updateContact(ContactProfile transientContact, Long id) {
        ContactProfile contact = contactProfileRepository.getContactProfileById(id);

        contact.setFirstName(transientContact.getFirstName());
        contact.setLastName(transientContact.getLastName());
        contact.setTitle(transientContact.getTitle());
        updateEmailAddresses(transientContact, contact);
        updatePhoneNumbers(transientContact, contact);

        return contact;
    }

    @Transactional
    public ContactProfile saveContact(ContactProfile contactProfile) {
        contactProfile.setUser(userRepository.findById(userId).orElseThrow());

        contactProfile.getPhoneNumbers()
                .stream()
                .filter(Objects::nonNull)
                .forEach(phoneNumber -> phoneNumber.setContactProfile(contactProfile));

        contactProfile.getEmailAddresses()
                .stream()
                .filter(Objects::nonNull)
                .forEach(emailAddress -> emailAddress.setContactProfile(contactProfile));

        return contactProfileRepository.save(contactProfile);
    }

    public void deleteContact(Long id) {
        contactProfileRepository.deleteById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
    }

    public boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }

    private static void updateEmailAddresses(ContactProfile contactDto, ContactProfile contact) {
        contactDto.getEmailAddresses().removeIf(Objects::isNull);

        updateEmailAddressByLabel(contactDto, contact, PERSONAL);
        updateEmailAddressByLabel(contactDto, contact, WORK);
    }

    private static void updatePhoneNumbers(ContactProfile contactDto, ContactProfile contact) {
        contactDto.getPhoneNumbers().removeIf(Objects::isNull);

        updatePhoneNumbersByLabel(contactDto, contact, PERSONAL);
        updatePhoneNumbersByLabel(contactDto, contact, WORK);
    }

    private static void updatePhoneNumbersByLabel(ContactProfile contactDto, ContactProfile contact, Label LABEL) {
        contactDto.getPhoneNumbers()
                .stream()
                .filter(phoneNumber -> phoneNumber.getPhoneLabel() == LABEL)
                .findAny()
                .ifPresent(phoneNumber -> contact.getPhoneNumbers()
                        .stream()
                        .filter(targetPhoneNumber -> targetPhoneNumber.getPhoneLabel() == LABEL)
                        .findAny()
                        .ifPresentOrElse(targetPhoneNumber -> targetPhoneNumber.setNumber(phoneNumber.getNumber()),
                                () -> {
                                    contact.getPhoneNumbers().add(phoneNumber);
                                    phoneNumber.setContactProfile(contact);
                                })
                );
    }

    private static void updateEmailAddressByLabel(ContactProfile contactDto, ContactProfile contact, Label LABEL) {
        contactDto.getEmailAddresses()
                .stream()
                .filter(emailAddress -> emailAddress.getEmailLabel() == LABEL)
                .findAny()
                .ifPresent(emailAddress -> contact.getEmailAddresses()
                        .stream()
                        .filter(targetEmailAddress -> targetEmailAddress.getEmailLabel() == LABEL)
                        .findAny()
                        .ifPresentOrElse(targetEmailAddress -> targetEmailAddress.setEmail(emailAddress.getEmail()),
                                () -> {
                                    contact.getEmailAddresses().add(emailAddress);
                                    emailAddress.setContactProfile(contact);
                                })
                );
    }
}
