package com.contact_management_system.repositories;

import com.contact_management_system.entities.ContactProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactProfileRepository extends JpaRepository<ContactProfile, Long> {

}