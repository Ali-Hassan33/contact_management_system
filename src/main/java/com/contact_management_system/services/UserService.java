package com.contact_management_system.services;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.entities.User;
import com.contact_management_system.enums.Label;
import com.contact_management_system.exceptions.EmailNotFoundException;
import com.contact_management_system.repositories.ContactProfileRepository;
import com.contact_management_system.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.contact_management_system.enums.Label.PERSONAL;
import static com.contact_management_system.enums.Label.WORK;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContactProfileRepository contactProfileRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ContactProfileRepository contactProfileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.contactProfileRepository = contactProfileRepository;
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

    @Transactional
    public ContactProfile updateContact(ContactProfile contactDto, Long id) {
        ContactProfile contact = contactProfileRepository.getContactProfileById(id);
        contact.setFirstName(contactDto.getFirstName());
        contact.setLastName(contactDto.getLastName());
        contact.setTitle(contactDto.getTitle());
        updateEmailAddresses(contactDto, contact);
        updatePhoneNumbers(contactDto, contact);
        return contact;
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
