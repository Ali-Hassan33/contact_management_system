package com.contact_management_system.repositories;

import com.contact_management_system.entities.Mime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.UUID;

public interface MimeRepository extends JpaRepository<Mime, UUID> {

    void deleteByCreatedAtBefore(Date createdAtBefore);
}
