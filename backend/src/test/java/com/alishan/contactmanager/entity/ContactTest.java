package com.alishan.contactmanager.entity;

import org.junit.jupiter.api.Test;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContactTest {

    @Test
    void setEmails_rejectsNullWithoutChangingExistingEmails() {
        Contact contact = new Contact();
        ContactEmail email = new ContactEmail();

        contact.addEmail(email);

        assertThatThrownBy(() ->
                contact.setEmails(Arrays.asList(new ContactEmail(), null)))
                .isInstanceOf(NullPointerException.class);

        assertThat(contact.getEmails()).containsExactly(email);
        assertThat(email.getContact()).isSameAs(contact);
    }

    @Test
    void setPhones_rejectsNullWithoutChangingExistingPhones() {
        Contact contact = new Contact();
        ContactPhone phone = new ContactPhone();

        contact.addPhone(phone);

        assertThatThrownBy(() ->
                contact.setPhones(Arrays.asList(new ContactPhone(), null)))
                .isInstanceOf(NullPointerException.class);

        assertThat(contact.getPhones()).containsExactly(phone);
        assertThat(phone.getContact()).isSameAs(contact);
    }
}