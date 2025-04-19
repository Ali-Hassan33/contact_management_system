package com.contact_management_system.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @OneToOne(fetch = LAZY)
    private User user;

    @CreationTimestamp
    private Date createdAt;
}
