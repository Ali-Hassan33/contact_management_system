package com.contact_management_system.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "contactProfile", fetch = EAGER)
    private List<EmailAddress> emailAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "contactProfile", fetch = EAGER)
    private List<EmailAddress> phoneNumbers = new ArrayList<>();
}