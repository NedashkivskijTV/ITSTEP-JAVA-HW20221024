package org.example;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id // позначає первинний ключ
    @Column(name = "id") // зв'язок поля з колонкою таблиці
    @GeneratedValue(strategy = GenerationType.IDENTITY) // визначає стратегію генерації унікального ідентифікатора - id
    private int id;

    @Column(name = "first_name") // зв'язок поля з колонкою таблиці
    private String firstName;

    @Column(name = "last_name") // зв'язок поля з колонкою таблиці
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contact")
    private List<Phone> phones = new ArrayList<>();

    public Contact() {
    }

    public Contact(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Contact(Contact contact) {
        this.id = contact.getId();
    }

    public int getId() {
        return id;
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

    public String getFullName() {
        return lastName + " " + firstName;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public void addPhone(Phone phone) {
        phones.add(phone);
        phone.setContact(this);
    }

    public void addPhones(Phone... newPhones) {
        for (Phone phone : newPhones) {
            addPhone(phone);
//            phones.add(phone);
//            phone.setContact(this);
        }
    }
}
