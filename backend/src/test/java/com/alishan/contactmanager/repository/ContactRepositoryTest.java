package com.alishan.contactmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.alishan.contactmanager.entity.*;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ContactRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EntityManager entityManager;

    private User saveUser() {
        User user = new User();
        user.setEmail("ali@example.com");
        user.setPasswordHash("hash");
        user.setFirstName("Ali");
        user.setLastName("Shan");
        return userRepository.save(user);
    }

    @Test
    void findByEmail_returnsSavedUser() {
        saveUser();

        assertThat(userRepository.findByEmail("ali@example.com")).isPresent();
        assertThat(userRepository.existsByEmail("none@example.com")).isFalse();
    }

    @Test
    void savingContact_savesItsEmailsAndPhones() {
        Contact contact = new Contact();
        contact.setUser(saveUser());
        contact.setFirstName("Sara");
        contact.setLastName("Khan");

        ContactEmail email = new ContactEmail();
        email.setLabel(ContactLabel.WORK);
        email.setEmail("sara@work.com");
        contact.addEmail(email);

        ContactPhone phone = new ContactPhone();
        phone.setLabel(ContactLabel.PERSONAL);
        phone.setPhoneNumber("03001234567");
        contact.addPhone(phone);

        Contact saved = contactRepository.saveAndFlush(contact);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmails()).hasSize(1);
        assertThat(saved.getPhones()).hasSize(1);
    }

    @Test
    void savingContactWithSetters_savesItsEmailsAndPhones() {
        Contact contact = new Contact();
        contact.setUser(saveUser());
        contact.setFirstName("Ahmed");
        contact.setLastName("Khan");

        ContactEmail email = new ContactEmail();
        email.setLabel(ContactLabel.WORK);
        email.setEmail("ahmed@work.com");

        ContactPhone phone = new ContactPhone();
        phone.setLabel(ContactLabel.PERSONAL);
        phone.setPhoneNumber("03001234567");

        contact.setEmails(List.of(email));
        contact.setPhones(List.of(phone));

        Contact saved = contactRepository.saveAndFlush(contact);
        Long contactId = saved.getId();

        entityManager.clear();

        Contact reloaded = contactRepository.findById(contactId)
                .orElseThrow();

        assertThat(reloaded.getEmails()).hasSize(1);
        assertThat(reloaded.getPhones()).hasSize(1);

        assertThat(reloaded.getEmails().get(0).getContact().getId())
                .isEqualTo(contactId);

        assertThat(reloaded.getPhones().get(0).getContact().getId())
                .isEqualTo(contactId);
    }
}