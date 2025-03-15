package com.contact_management_system.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactProfile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String title;

    @ManyToOne(cascade = PERSIST)
    private User user;

    @OneToMany(cascade = PERSIST, fetch = EAGER, mappedBy = "contactProfile")
    private List<EmailAddress> emailAddresses;

    @OneToMany(cascade = PERSIST, fetch = EAGER, mappedBy = "contactProfile")
    private List<PhoneNumber> phoneNumbers;
}