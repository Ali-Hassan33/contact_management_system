package com.contact_management_system.entities;

import com.contact_management_system.enums.Label;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Label emailLabel;

    @ManyToOne(cascade = PERSIST)
    @JsonBackReference
    private ContactProfile contactProfile;
}

