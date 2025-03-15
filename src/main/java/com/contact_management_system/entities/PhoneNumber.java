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
public class PhoneNumber {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
// todo type ? string:integer
    private String number;

    @Enumerated(STRING)
    private EmailLabel phoneLabel;

    @ManyToOne(cascade = {PERSIST, REMOVE})
    private ContactProfile contactProfile;
}

