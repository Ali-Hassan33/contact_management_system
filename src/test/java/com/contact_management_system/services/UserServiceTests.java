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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ContactProfileRepository contactProfileRepository;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private ContactProfile testContactProfile;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        Map<String, Object> tokenAttributes = Map.of("id", USER_ID);
        lenient().when(jwtAuthenticationToken.getTokenAttributes()).thenReturn(tokenAttributes);

        testContactProfile = createTestContactProfile();
    }

    @Test
    @DisplayName("Should get contacts for authenticated user")
    void testGetContacts() {
        List<ContactProfile> contacts = List.of(testContactProfile);
        Page<ContactProfile> contactsPage = new PageImpl<>(contacts);

        when(contactProfileRepository.findAllByUserId(eq(USER_ID), any(Pageable.class)))
                .thenReturn(contactsPage);

        Page<ContactProfile> result = userService.getContacts(jwtAuthenticationToken);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testContactProfile, result.getContent().getFirst());
        verify(contactProfileRepository).findAllByUserId(eq(USER_ID), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get paginated contacts for authenticated user")
    void testGetContactsPaginated() {
        List<ContactProfile> contacts = List.of(testContactProfile);
        Page<ContactProfile> contactsPage = new PageImpl<>(contacts);
        int pageNo = 0;
        int pageSize = 10;

        when(contactProfileRepository.findAllByUserId(eq(USER_ID), any(PageRequest.class)))
                .thenReturn(contactsPage);

        Page<ContactProfile> result = userService.getContactsPaginated(jwtAuthenticationToken, pageNo, pageSize);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testContactProfile, result.getContent().getFirst());
        verify(contactProfileRepository).findAllByUserId(eq(USER_ID), any(PageRequest.class));
    }

    @Test
    @DisplayName("Should save basic auth user")
    void testSaveBasicAuthUser() {
        UserDto userDto = UserDto.builder()
                .name("New User")
                .email("new@example.com")
                .password("password123")
                .build();

        String encodedPassword = "encodedPassword123";
        User savedUser = new User("New User", "new@example.com", encodedPassword);

        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.saveBasicAuthUser(userDto);

        assertNotNull(result);
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getPassword(), result.getPassword());
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should save OAuth2 user")
    void testSaveOAuth2User() {
        UserDto userDto = UserDto.builder()
                .name("OAuth User")
                .email("oauth@example.com")
                .build();

        User savedUser = new User("OAuth User", "oauth@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.saveOAuth2User(userDto);

        assertNotNull(result);
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should save contact")
    void testSaveContact() {
        ContactProfile contactToSave = createTestContactProfile();
        contactToSave.setId(null);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(contactProfileRepository.save(any(ContactProfile.class))).thenReturn(testContactProfile);

        userService.getContacts(jwtAuthenticationToken);

        ContactProfile result = userService.saveContact(contactToSave);

        assertNotNull(result);
        assertEquals(testContactProfile, result);
        verify(userRepository).findById(USER_ID);
        verify(contactProfileRepository).save(contactToSave);

        assertEquals(testUser, contactToSave.getUser());
        contactToSave.getEmailAddresses().forEach(email ->
                assertEquals(contactToSave, email.getContactProfile()));
        contactToSave.getPhoneNumbers().forEach(phone ->
                assertEquals(contactToSave, phone.getContactProfile()));
    }

    @Test
    @DisplayName("Should update contact")
    void testUpdateContact() {
        ContactProfile updatedContact = createTestContactProfile();
        updatedContact.setFirstName("Updated First");
        updatedContact.setLastName("Updated Last");

        when(contactProfileRepository.getContactProfileById(testContactProfile.getId()))
                .thenReturn(testContactProfile);

        ContactProfile result = userService.updateContact(updatedContact, testContactProfile.getId());

        assertNotNull(result);
        assertEquals("Updated First", result.getFirstName());
        assertEquals("Updated Last", result.getLastName());
        verify(contactProfileRepository).getContactProfileById(testContactProfile.getId());
    }

    @Test
    @DisplayName("Should delete contact")
    void testDeleteContact() {
        userService.deleteContact(testContactProfile.getId());

        verify(contactProfileRepository).deleteById(testContactProfile.getId());
    }

    @Test
    @DisplayName("Should get user by email")
    void testGetUserByEmail() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        User result = userService.getUserByEmail(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should throw EmailNotFoundException when user not found")
    void testGetUserByEmailNotFound() {
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        assertThrows(EmailNotFoundException.class, () -> userService.getUserByEmail(nonExistentEmail));
        verify(userRepository).findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("Should check if user exists")
    void testIsUserExist() {
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        assertTrue(userService.isUserExist(testUser.getEmail()));
        assertFalse(userService.isUserExist("nonexistent@example.com"));
        verify(userRepository, times(2)).existsByEmail(anyString());
    }

    private ContactProfile createTestContactProfile() {
        EmailAddress workEmail = EmailAddress.builder()
                .email("john.doe@company.com")
                .emailLabel(Label.WORK)
                .build();

        EmailAddress personalEmail = EmailAddress.builder()
                .email("john.doe@example.com")
                .emailLabel(Label.PERSONAL)
                .build();

        PhoneNumber workPhone = PhoneNumber.builder()
                .number("555-123-4567")
                .phoneLabel(Label.WORK)
                .build();

        PhoneNumber personalPhone = PhoneNumber.builder()
                .number("555-987-6543")
                .phoneLabel(Label.PERSONAL)
                .build();

        ContactProfile contactProfile = ContactProfile.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .title("Software Engineer")
                .emailAddresses(new ArrayList<>(Arrays.asList(workEmail, personalEmail)))
                .phoneNumbers(new ArrayList<>(Arrays.asList(workPhone, personalPhone)))
                .build();

        workEmail.setContactProfile(contactProfile);
        personalEmail.setContactProfile(contactProfile);
        workPhone.setContactProfile(contactProfile);
        personalPhone.setContactProfile(contactProfile);

        return contactProfile;
    }
}
