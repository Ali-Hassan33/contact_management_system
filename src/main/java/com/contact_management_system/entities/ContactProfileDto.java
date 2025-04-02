package com.contact_management_system.entities;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link ContactProfile}
 */
@Value
public class ContactProfileDto implements Serializable {
    String firstName;
    String lastName;
    String title;
    List<EmailAddress> emailAddresses;
    List<PhoneNumber> phoneNumbers;
}