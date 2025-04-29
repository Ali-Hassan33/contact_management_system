package com.contact_management_system.services;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.entities.EmailAddress;
import com.contact_management_system.entities.PhoneNumber;
import com.contact_management_system.entities.User;
import com.contact_management_system.enums.Label;
import com.contact_management_system.exceptions.EmailNotFoundException;
import com.contact_management_system.repositories.ContactProfileRepository;
import com.contact_management_system.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.contact_management_system.enums.Label.PERSONAL;
import static com.contact_management_system.enums.Label.WORK;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContactProfileRepository contactProfileRepository;
    private Long authenticatedUserId;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ContactProfileRepository contactProfileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.contactProfileRepository = contactProfileRepository;
    }

    public Page<ContactProfile> getContacts(Authentication authentication) {
        syncAuthenticatedUserId(authentication);
        log.info("Fetching all contacts for userId: {}", authenticatedUserId);
        return contactProfileRepository.findAllByUserId(authenticatedUserId, Pageable.unpaged());
    }

    public Page<ContactProfile> getContactsPaginated(Authentication authentication, Integer pageNo, Integer pageSize) {
        syncAuthenticatedUserId(authentication);
        log.info("Fetching paginated contacts for userId: {}, page: {}, pageSize: {}", authenticatedUserId, pageNo, pageSize);
        return contactProfileRepository.findAllByUserId(authenticatedUserId, PageRequest.of(pageNo, pageSize));
    }

    @Transactional
    public User saveBasicAuthUser(UserDto userDto) {
        return persist(new User(userDto.getName(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword())));
    }

    @Transactional
    public User saveOAuth2User(UserDto userDto) {
        return persist(new User(userDto.getName(), userDto.getEmail()));
    }

    @Transactional
    public ContactProfile saveContact(ContactProfile contactProfile) {
        log.info("Saving contact: {}", contactProfile);
        contactProfile.setUser(userRepository.findById(authenticatedUserId).orElseThrow());

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

    @Transactional
    public ContactProfile updateContact(ContactProfile transientContact, Long id) {
        log.info("Updating contact with ID: {}", id);
        ContactProfile contact = contactProfileRepository.getContactProfileById(id);

        contact.setFirstName(transientContact.getFirstName());
        contact.setLastName(transientContact.getLastName());
        contact.setTitle(transientContact.getTitle());
        updateContactEmails(transientContact, contact);
        updateContactNumbers(transientContact, contact);

        return contact;
    }

    public void deleteContact(Long id) {
        log.info("Deleting contact with id {}", id);
        contactProfileRepository.deleteById(id);
    }

    public void deleteContacts(List<Long> ids) {
        log.info("Deleting contacts with IDs: {}", ids);
        contactProfileRepository.deleteAllById(ids);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));
    }

    public boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }

    private void syncAuthenticatedUserId(Authentication authentication) {
        this.authenticatedUserId = Optional.of(authentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(jwt -> jwt.getTokenAttributes().get("id"))
                .map(Long.class::cast)
                .orElseThrow(RuntimeException::new);
    }

    private User persist(User user) {
        return userRepository.save(user);
    }

    private static void updateContactEmails(ContactProfile contactDto, ContactProfile contact) {
        contactDto.getEmailAddresses().removeIf(Objects::isNull);

        syncContactEmail(contactDto, contact, PERSONAL);
        syncContactEmail(contactDto, contact, WORK);
    }

    private static void syncContactEmail(ContactProfile contactDto, ContactProfile contact, Label LABEL) {
        contactDto.getEmailAddresses()
                .stream()
                .filter(emailAddress -> emailAddress.getEmailLabel() == LABEL)
                .findAny()
                .ifPresentOrElse(
                        emailAddress -> updateOrAddContactEmail(emailAddress, contact, LABEL),
                        () -> contact.getEmailAddresses().removeIf(targetEmailAddress -> targetEmailAddress.getEmailLabel() == LABEL)
                );
    }

    private static void updateContactNumbers(ContactProfile contactDto, ContactProfile contact) {
        contactDto.getPhoneNumbers().removeIf(Objects::isNull);

        syncContactNumber(contactDto, contact, PERSONAL);
        syncContactNumber(contactDto, contact, WORK);
    }

    private static void syncContactNumber(ContactProfile contactDto, ContactProfile contact, Label LABEL) {
        contactDto.getPhoneNumbers()
                .stream()
                .filter(phoneNumber -> phoneNumber.getPhoneLabel() == LABEL)
                .findAny()
                .ifPresentOrElse(
                        phoneNumber -> updateOrAddContactNumber(phoneNumber, contact, LABEL),
                        () -> contact.getPhoneNumbers().removeIf(targetPhoneNumber -> targetPhoneNumber.getPhoneLabel() == LABEL)
                );
    }

    private static void addContactEmail(EmailAddress emailAddress, ContactProfile contact) {
        contact.getEmailAddresses().add(emailAddress);
        emailAddress.setContactProfile(contact);
    }

    private static void addContactNumber(PhoneNumber phoneNumber, ContactProfile contact) {
        contact.getPhoneNumbers().add(phoneNumber);
        phoneNumber.setContactProfile(contact);
    }

    private static void updateOrAddContactNumber(PhoneNumber phoneNumber, ContactProfile contact, Label LABEL) {
        contact.getPhoneNumbers()
                .stream()
                .filter(existingPhoneNumber -> existingPhoneNumber.getPhoneLabel() == LABEL)
                .findAny()
                .ifPresentOrElse(
                        existingPhoneNumber -> existingPhoneNumber.setNumber(phoneNumber.getNumber()),
                        () -> addContactNumber(phoneNumber, contact)
                );
    }

    private static void updateOrAddContactEmail(EmailAddress emailAddress, ContactProfile contact, Label LABEL) {
        contact.getEmailAddresses()
                .stream()
                .filter(existingEmailAddress -> existingEmailAddress.getEmailLabel() == LABEL)
                .findAny()
                .ifPresentOrElse(
                        existingEmailAddress -> existingEmailAddress.setEmail(emailAddress.getEmail()),
                        () -> addContactEmail(emailAddress, contact)
                );
    }

}
