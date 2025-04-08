package com.contact_management_system.repositories;

import com.contact_management_system.entities.ContactProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactProfileRepository extends JpaRepository<ContactProfile, Long> {

    @Query("select c from ContactProfile c where c.user.id = ?1")
    Page<ContactProfile> findAllByUserId(Long userId, Pageable pageable);

    ContactProfile getContactProfileById(Long id);
}