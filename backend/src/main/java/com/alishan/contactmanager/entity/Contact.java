package com.alishan.contactmanager.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String title;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactEmail> emails = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactPhone> phones = new ArrayList<>();

    public void addEmail(ContactEmail email) {
        emails.add(email);
        email.setContact(this);
    }

    public void addPhone(ContactPhone phone) {
        phones.add(phone);
        phone.setContact(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ContactEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<ContactEmail> emails) {
        List<ContactEmail> newEmails = List.copyOf(emails);

        this.emails.forEach(email -> email.setContact(null));
        this.emails.clear();

        newEmails.forEach(this::addEmail);
    }

    public List<ContactPhone> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactPhone> phones) {
        List<ContactPhone> newPhones = List.copyOf(phones);

        this.phones.forEach(phone -> phone.setContact(null));
        this.phones.clear();

        newPhones.forEach(this::addPhone);
    }
}