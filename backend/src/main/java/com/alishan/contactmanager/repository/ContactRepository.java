package com.alishan.contactmanager.repository;

import com.alishan.contactmanager.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}