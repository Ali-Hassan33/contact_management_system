package com.contact_management_system.entities;

import com.contact_management_system.enums.EmailLabel;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAddress {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String email;

    @Enumerated(STRING)
    private EmailLabel emailLabel;

    @ManyToOne(cascade = {PERSIST, REMOVE})
    private ContactProfile contactProfile;
}

