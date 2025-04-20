package com.contact_management_system.controllers;

import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.entities.EmailAddress;
import com.contact_management_system.entities.PhoneNumber;
import com.contact_management_system.enums.Label;
import com.contact_management_system.services.CSVService;
import com.contact_management_system.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserControllerTests.TestSecurityConfig.class)
class UserControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    CSVService csvService;

    @Test
    @DisplayName("Should retrieve paginated contacts for authenticated user")
    @WithMockUser
    void testGetContactsByPage() throws Exception {
        ContactProfile mockProfile = createMockContactProfile();
        List<ContactProfile> contacts = List.of(mockProfile);
        Page<ContactProfile> contactsPage = new PageImpl<>(contacts);

        when(userService.fetchContactsByPage(any(Authentication.class), eq(0), eq(10)))
                .thenReturn(contactsPage);

        mockMvc.perform(get("/api/contacts")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("John")))
                .andExpect(jsonPath("$.content[0].lastName", is("Doe")))
                .andExpect(jsonPath("$.content[0].title", is("Software Engineer")))
                .andExpect(jsonPath("$.content[0].emailAddresses", hasSize(2)))
                .andExpect(jsonPath("$.content[0].phoneNumbers", hasSize(2)));

        verify(userService).fetchContactsByPage(any(Authentication.class), eq(0), eq(10));
    }

    @Test
    @DisplayName("Should retrieve all contacts for authenticated user")
    @WithMockUser
    void testGetAllContacts() throws Exception {
        ContactProfile mockProfile = createMockContactProfile();
        List<ContactProfile> contacts = List.of(mockProfile);
        Page<ContactProfile> contactsPage = new PageImpl<>(contacts);

        when(userService.fetchContacts(any(Authentication.class)))
                .thenReturn(contactsPage);

        mockMvc.perform(get("/api/contacts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("John")))
                .andExpect(jsonPath("$.content[0].lastName", is("Doe")))
                .andExpect(jsonPath("$.content[0].title", is("Software Engineer")))
                .andExpect(jsonPath("$.content[0].emailAddresses", hasSize(2)))
                .andExpect(jsonPath("$.content[0].emailAddresses[0].email", is("john.doe@company.com")))
                .andExpect(jsonPath("$.content[0].emailAddresses[1].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.content[0].phoneNumbers", hasSize(2)))
                .andExpect(jsonPath("$.content[0].phoneNumbers[0].number", is("555-123-4567")))
                .andExpect(jsonPath("$.content[0].phoneNumbers[1].number", is("555-987-6543")));

        verify(userService).fetchContacts(any(Authentication.class));
    }

    @Test
    @DisplayName("Should save new contact and return saved contact details")
    @WithMockUser
    void testSaveContact() throws Exception {
        ContactProfile mockProfile = createMockContactProfile();

        when(userService.saveContact(any(ContactProfile.class)))
                .thenReturn(mockProfile);

        mockMvc.perform(post("/api/contact/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.title", is("Software Engineer")))
                .andExpect(jsonPath("$.emailAddresses", hasSize(2)))
                .andExpect(jsonPath("$.emailAddresses[0].email", is("john.doe@company.com")))
                .andExpect(jsonPath("$.emailAddresses[0].emailLabel", is("WORK")))
                .andExpect(jsonPath("$.emailAddresses[1].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.emailAddresses[1].emailLabel", is("PERSONAL")))
                .andExpect(jsonPath("$.phoneNumbers", hasSize(2)))
                .andExpect(jsonPath("$.phoneNumbers[0].number", is("555-123-4567")))
                .andExpect(jsonPath("$.phoneNumbers[0].phoneLabel", is("WORK")))
                .andExpect(jsonPath("$.phoneNumbers[1].number", is("555-987-6543")))
                .andExpect(jsonPath("$.phoneNumbers[1].phoneLabel", is("PERSONAL")));

        verify(userService).saveContact(any(ContactProfile.class));
    }

    @Test
    @DisplayName("Should update existing contact and return updated contact details")
    @WithMockUser
    void testUpdateContact() throws Exception {
        ContactProfile mockProfile = createMockContactProfile();

        when(userService.updateContact(any(ContactProfile.class), eq(1L)))
                .thenReturn(mockProfile);

        mockMvc.perform(put("/api/contact/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.title", is("Software Engineer")))
                .andExpect(jsonPath("$.emailAddresses", hasSize(2)))
                .andExpect(jsonPath("$.emailAddresses[0].email", is("john.doe@company.com")))
                .andExpect(jsonPath("$.emailAddresses[0].emailLabel", is("WORK")))
                .andExpect(jsonPath("$.emailAddresses[1].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.emailAddresses[1].emailLabel", is("PERSONAL")))
                .andExpect(jsonPath("$.phoneNumbers", hasSize(2)))
                .andExpect(jsonPath("$.phoneNumbers[0].number", is("555-123-4567")))
                .andExpect(jsonPath("$.phoneNumbers[0].phoneLabel", is("WORK")))
                .andExpect(jsonPath("$.phoneNumbers[1].number", is("555-987-6543")))
                .andExpect(jsonPath("$.phoneNumbers[1].phoneLabel", is("PERSONAL")));

        verify(userService).updateContact(any(ContactProfile.class), eq(1L));
    }

    @Test
    @DisplayName("Should delete contact by ID")
    @WithMockUser
    void testDeleteContact() throws Exception {
        mockMvc.perform(delete("/api/contact/1"))
                .andExpect(status().isOk());

        verify(userService).deleteContact(1L);
    }

    @Test
    @DisplayName("Should import contacts from CSV file")
    @WithMockUser
    void testImportContacts() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "csvFile",
                "contacts.csv",
                "text/csv",
                "firstName,lastName,email\nJohn,Doe,john@example.com".getBytes()
        );

        mockMvc.perform(multipart("/api/contacts/import")
                        .file(file))
                .andExpect(status().isOk());

        verify(csvService).importCsv(any());
    }

    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    private ContactProfile createMockContactProfile() {
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
                .id(1L)
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
