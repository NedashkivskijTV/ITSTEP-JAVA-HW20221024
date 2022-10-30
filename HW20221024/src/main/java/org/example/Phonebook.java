package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Phonebook {

    private List<Contact> contactList = new ArrayList<>(); // список контактів
    private List<List<Phone>> phoneLists = new ArrayList<>();

    private final int LINE_SIZE = 26; // довжина рядка при роздруку у консоль

    private String[][] systemLines = new String[][]{
            new String[]{"yes", "y", "", "no", "n"},
            new String[]{"y", "yes", ""},
            new String[]{"1", "2", "3", "4", "0"},
            new String[]{
                    "Переглянути контакти",
                    "Додати дані",
                    "Змінити контакт",
                    "Видалити дані"
            },
            new String[]{
                    "Видалити контакт",
                    "Видалити телефон",
            },
            new String[]{
                    "Додати контакт",
                    "Додати телефон",
            },
    };

    // Основний метод роботи з довідником
    public void start() {
        System.out.println("ТЕЛЕФОННИЙ ДОВІДНИК");
        System.out.println("Бажаєте скористатись можливостями довідника ? (" + stringMasToString(systemLines[0]) + ") - ");
        if (!lineCheck(lineInputControl(""), systemLines[1])) { // "y", "yes", "")
            return;
        }
        String userChoice = "";
        //Scanner scanner = new Scanner(System.in);
        do {
            contactList.clear();
            phoneLists.clear();

            userMenu("  ТЕЛЕФОННИЙ ДОВІДНИК", systemLines[3]);

            userChoice = lineInputControl("Допустимі значення: 0" + (systemLines[3].length == 0 ? "" : " - " + systemLines[3].length), masIndexesToMasStrings(systemLines[3].length));

            if (!userChoice.equals("0")) {
                phonebookActions(userChoice);
            }

        } while (!userChoice.equals("0"));
        System.out.println("\nРоботу програми завершено");
    }

    // Виведення системного меню
    private void userMenu(String header, String... menuItems) {
        String futer = "- Вихід";
        String choiceLine = "- Оберіть потрібну дію";
        int count = 1;
        System.out.println();
        System.out.println(header);
        for (String menuItem : menuItems) {
            System.out.print("- ");
            System.out.print(menuItem);
            printSpace(LINE_SIZE - menuItem.length() > 0 ? LINE_SIZE - menuItem.length() : 1);
            System.out.print("- ");
            System.out.println(count++);
        }
        System.out.println("--------------------------------");
        System.out.print(futer);
        printSpace(LINE_SIZE - futer.length() > 0 ? LINE_SIZE - futer.length() : 1);
        System.out.println("  - 0");
        System.out.print(choiceLine);
        printSpace(LINE_SIZE - choiceLine.length() > 0 ? LINE_SIZE - choiceLine.length() : 1);
        System.out.print("  - ");
    }

    // Виведення (зяповнення) рядка, що складається з заданої кількості пробілів
    private void printSpace(int spaceCount) {
        for (int i = 0; i < spaceCount; i++) {
            System.out.print(" ");
        }
    }

    // Забезпечення виклику відповідного алгоритму (переглянути / додати / змінити / видалити)
    // в залежності від обраної користувачем дії
    private void phonebookActions(String choice) {
        boolean weitForeUser = true;
        switch (choice) {
            case "1": // Переглянути контакти // -----------------------------------------------------------------------
            {
                showAllContacts();
                break;
            }
            case "2": // Додати контакт / телефон  // ------------------------------------------------------------------
            {
                addMenu();
                break;
            }
            case "3": // Змінити контакт // ----------------------------------------------------------------------------
            {
                editContact();
                break;
            }
            case "4": // Видалити контакт / телефон // -----------------------------------------------------------------
            {
                userMenu("  МЕНЮ ВИДАЛЕННЯ", systemLines[4]);

                String userDellChoice = lineInputControl("Допустимі значення: 0" + (systemLines[4].length == 0 ? "" : " - " + systemLines[4].length), masIndexesToMasStrings(systemLines[4].length));

                if (!userDellChoice.equals("0")) {
                    phonebookDellActions(userDellChoice);
                } else {
                    weitForeUser = false;
                }
                break;
            }
        }
        if (weitForeUser) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nДля продовження натисніть Enter");
            scanner.nextLine();
        } else {
            System.out.println("\n\n");
        }
    }

    // Проміжний метод при читанні з БД та виведенні списку контактів
    private void showAllContacts() {
        databaseRequests(1, null, null);
        System.out.println("\n    Список контактів у довіднику :");
        printContactsWithPhones(true);
    }

    // Виведення списку контактів з телефонами / без телефонів
    private void printContactsWithPhones(boolean printPhones) {
        //System.out.println("    Список контактів у довіднику :");
        System.out.println("----------------------------------");
        for (int i = 0; i < contactList.size(); i++) {
            System.out.print("" + (i + 1) + " - ");
            printContactById(i);
            if (printPhones) {
                printPhonesByContactId(i);
            }
        }
    }

    // Виведення контакту за id у раніше завантаженому списку контактів (поле contactList)
    private void printContactById(int contactId) {
        System.out.println(contactList.get(contactId).getLastName() + " " + contactList.get(contactId).getFirstName());
    }

    // Виведення списку телефонів за id контакту у раніше завантаженому списку контактів (поля contactList, phoneLists)
    private void printPhonesByContactId(int contactId) {
        for (Phone phone : phoneLists.get(contactId)) {
            System.out.println("         " + phone.getPhoneNumber());
        }
    }

    // Меню додавання інформації
    private void addMenu() {
        userMenu("  ЯКІ ДАНІ БАЖАЄТЕ ДОДАТИ", systemLines[5]);

        String userAddChoice = lineInputControl("Допустимі значення: 0" + (systemLines[5].length == 0 ? "" : " - " + systemLines[5].length), masIndexesToMasStrings(systemLines[5].length));

        if (!userAddChoice.equals("0")) {
            phonebookAddActions(userAddChoice);
        } else {
            System.out.println("\nДодавання даних відмінено");
        }
    }

    // Забезпечення алгоритму виклику певного алгоритму додавання даних в залежності від вибору користувача
    private void phonebookAddActions(String userAddChoice) {
        switch (userAddChoice) {
            case "1": {
                addContact();
                break;
            }
            case "2": {
                addPhone();
                break;
            }
        }
    }

    // Додавання контакту
    private void addContact() {
        String newContactFirstName = "", newContactLastName = "", newContactPhoneNumber = "";
        List<Phone> newContactPhones = new ArrayList<>();
        System.out.println("\nВведіть дані нового контакта та натисніть Enter :");
        System.out.print("   Прізвище       - ");
        newContactLastName = inputText();
        System.out.print("   Ім'я           - ");
        newContactFirstName = inputText();
        System.out.println("Номер телефону або 0, щоб відмовитись - ");
        do {
            System.out.print("  - введіть номер - ");
            newContactPhoneNumber = inputText();
            if (!newContactPhoneNumber.equals("0")) {
                newContactPhones.add(new Phone(newContactPhoneNumber));
            }
        } while (!newContactPhoneNumber.equals("0"));

        Contact newContact = new Contact(newContactFirstName, newContactLastName); // створення нового контакта

        // приведення колекції телефонів нового контакта до масиву об'єктів Phone (передається у якості
        // аргумента змінної довжини до методу, що долучає масив телефонів до контакту)
        Phone[] phonesArr = new Phone[newContactPhones.size()];
        newContactPhones.toArray(phonesArr);

        newContact.addPhones(phonesArr); // долучення масиву телефонів до контакта

        databaseRequests(2, newContact, null); // додавання нового контакта до БД
    }

    // Додавання телефону до обраного контакту
    private void addPhone() {
        String userChoiceOfContactToAddPhone = menuOfContacts();
        if (userChoiceOfContactToAddPhone.equals("0")) {
            System.out.println("\nДожавання відмінено");
            return;
        }

        List<Phone> newContactPhones = new ArrayList<>();
        int indexContact = Integer.parseInt(userChoiceOfContactToAddPhone) - 1;

        System.out.print("\nОбрано контакт - ");
        printContactById(indexContact);

        String newContactPhoneNumber = "";
        Contact selectedContact = new Contact(contactList.get(indexContact));
        System.out.println("Введіть номер телефону або 0, щоб відмовитись - ");
        do {
            System.out.print("  - введіть номер - ");
            newContactPhoneNumber = inputText();
            if (!newContactPhoneNumber.equals("0")) {
                newContactPhones.add(new Phone(newContactPhoneNumber));
            }
        } while (!newContactPhoneNumber.equals("0"));

        if (newContactPhones.size() != 0) { // виконання звернення до БД та підготовчих маніпуляцій лише у випадку наявності введених телефонів
            Contact newContact = new Contact(selectedContact); // створення нового контакта

            // приведення колекції нових телефонів контакта до масиву об'єктів Phone (передається у якості
            // аргумента змінної довжини до методу, що долучає масив телефонів до контакту)
            Phone[] phonesArr = new Phone[newContactPhones.size()];
            newContactPhones.toArray(phonesArr);

            newContact.addPhones(phonesArr); // долучення масиву телефонів до контакта

            databaseRequests(2, newContact, new Phone()); // додавання нового контакта до БД
        }
    }

    // Редагування інформації про контакт
    private void editContact() {
        String userChoiceOfContactToEdit = menuOfContacts();

        if (!userChoiceOfContactToEdit.equals("0")) {
            boolean isFieldsChanged = false;
            int indexContact = Integer.parseInt(userChoiceOfContactToEdit) - 1;
            Contact tempContact = new Contact(contactList.get(indexContact));

            System.out.print("\n\nРЕДАГУВАННЯ КОНТАКТА: ");
            printContactById(indexContact);

            System.out.print("\nРедагувати прізвище ? (" + stringMasToString(systemLines[0]) + ") - ");
            if (lineCheck(lineInputControl(""), systemLines[1])) { // "y", "yes", "")
                System.out.print("Введіть прізвище - ");
                tempContact.setLastName(inputText());
                isFieldsChanged = true;
            } else {
                tempContact.setLastName(contactList.get(indexContact).getLastName());
            }

            System.out.print("\nРедагувати ім'я ? (" + stringMasToString(systemLines[0]) + ") - ");
            if (lineCheck(lineInputControl(""), systemLines[1])) { // "y", "yes", "")
                System.out.print("Введіть ім'я - ");
                tempContact.setFirstName(inputText());
                isFieldsChanged = true;
            } else {
                tempContact.setFirstName(contactList.get(indexContact).getFirstName());
            }

            for (Phone phone : phoneLists.get(indexContact)) {
                System.out.println("\nЗмінити номер телефону " + phone.getPhoneNumber() + " ? (" + stringMasToString(systemLines[0]) + ") - ");
                if (lineCheck(lineInputControl(""), systemLines[1])) { // "y", "yes", "")
                    System.out.print("Введіть номер телефону - ");
                    tempContact.addPhone(new Phone(inputText()));
                    isFieldsChanged = true;
                } else {
                    tempContact.addPhone(new Phone(phone.getPhoneNumber()));
                }
            }

            if (isFieldsChanged) { // запит до БД лише у випадку внесення змін
                databaseRequests(3, tempContact, null);
            }

        } else {
            System.out.println("\nРедагування контакту відмінено");
        }
    }

    // Метод для забезпечення введення текстової інформації
    private String inputText() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    // Забезпечення алгоритму видалення даних в залежності від вибору користувача
    private void phonebookDellActions(String userDellChoice) {
        databaseRequests(1, null, null);

        String[] contactsFullName = new String[contactList.size()]; // отримання списку повних імен контактів
        for (int i = 0; i < contactList.size(); i++) {
            contactsFullName[i] = contactList.get(i).getFullName();
        }

        userMenu("Оберіть номер контаку зі списку :", contactsFullName);

        String userDellContactChoice = lineInputControl("Допустимі значення: 0" + (contactList.size() == 0 ? "" : " - " + contactList.size()), masIndexesToMasStrings(contactList.size()));

        if (userDellContactChoice.equals("0")) {
            System.out.println("\nВидалення відмінено");
            return;
        }

        int indexContact = Integer.parseInt(userDellContactChoice) - 1; // отримання індекса обраного контакта

        switch (userDellChoice) {
            case "1": { // Видалити контакт
                databaseRequests(4, contactList.get(indexContact), null);
                System.out.println("\nДані видалено");
                break;
            }
            case "2": { // Видалити телефон
                System.out.println();
                printContactById(indexContact);

                String[] phonesToDell = listPhonesToMasStrings(phoneLists.get(indexContact));

                userMenu("  СПИСОК ТЕЛЕФОНІВ КОНТАКТУ", phonesToDell);

                String userDellPhoneChoice = lineInputControl("Допустимі значення: 0" + (phoneLists.get(indexContact).size() == 0 ? "" : " - " + phoneLists.get(indexContact).size()), masIndexesToMasStrings(phoneLists.get(indexContact).size()));

                if (!userDellPhoneChoice.equals("0")) {
                    int indexPhone = Integer.parseInt(userDellPhoneChoice) - 1; // отримання індекса обраного для видалення телефону
                    databaseRequests(4, null, phoneLists.get(indexContact).get(indexPhone));
                    System.out.println("\nДані видалено");
                } else {
                    System.out.println("\nВидалення телефону відмінено");
                }
                break;
            }
        }
    }

    // Меню для вибору контакта зі списку
    private String menuOfContacts() {
        databaseRequests(1, null, null);

        String[] contactsFullName = new String[contactList.size()]; // отримання списку повних імен контактів
        for (int i = 0; i < contactList.size(); i++) {
            contactsFullName[i] = contactList.get(i).getFullName();
        }

        userMenu("Оберіть номер контаку зі списку :", contactsFullName); // виведення меню - списку контактів

        // метод контрольованого вводу даних користувачем
        String userChoice = lineInputControl("Допустимі значення: 0" + (contactList.size() == 0 ? "" : " - " + contactList.size()), masIndexesToMasStrings(contactList.size()));

        return userChoice;
    }

    // Контроль коректності вводу інформації користувачем
    private String lineInputControl(String message, String... correctLine) {
        if (message == null || message.length() == 0) {
            message = "Введіть yes/no та Enter";
        }
        if (correctLine == null || correctLine.length == 0) {
            correctLine = systemLines[0]; // "yes", "no", "y", "n", ""
        }
        Scanner scanner = new Scanner(System.in);
        String userLine = scanner.nextLine();
        while (!lineCheck(userLine, correctLine)) {
            System.out.println("\n" + message);
            userLine = scanner.nextLine();
        }
        return userLine;
    }

    // Перевірка наявності рядка у списку рядків
    private boolean lineCheck(String line, String... possibleLines) {
        for (String possibleLine : possibleLines) {
            if (possibleLine.equalsIgnoreCase(line)) {
                return true;
            }
        }
        return false;
    }

    // Пеетворення масиву рядків на рядок
    private String stringMasToString(String... strings) {
        if (strings.length == 0) {
            return "";
        }
        StringBuffer strTemp = new StringBuffer("");
        for (String string : strings) {
            if (strTemp.length() != 0) {
                strTemp.append(" ");
            }
            strTemp.append(string.length() == 0 ? "Enter" : string);
        }
        return strTemp.toString();
    }

    // Перетворення числа (довжина масиву) на масив типу String[] індексів (+1)
    private String[] masIndexesToMasStrings(int masLenght) {
        String[] indexesLine = new String[masLenght + 1];
        for (int i = 0; i <= masLenght; i++) {
            indexesLine[i] = "" + i;
        }
        return indexesLine;
    }

    // Перетворення колекції елементів типу Phone на масив рядків з телефонними комерами
    private String[] listPhonesToMasStrings(List<Phone> phonesList) {
        int masSize = phonesList.size();
        String[] stringsMas = new String[masSize];
        for (int i = 0; i < masSize; i++) {
            stringsMas[i] = phonesList.get(i).getPhoneNumber();
        }
        return stringsMas;
    }

    // Метод забезпечення роботи з БД (усі запити)
    private void databaseRequests(int action, Contact selectedContact, Phone selectedPhone) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Phone.class)
                .addAnnotatedClass(Contact.class)
                .buildSessionFactory();
        Session session = factory.getCurrentSession(); // створення сесії
        session.beginTransaction(); // відкриття сесії

        switch (action) {
            case 1: { // READ ALL --------------------------------------------------------------------------------------
                contactList = session.createQuery("from Contact").getResultList();
                for (Contact contact : contactList) {
                    phoneLists.add(session.createQuery("from Phone where contact_id = '" + contact.getId() + "'").getResultList());
                }
                break;
            }
            case 2: { // CREATE - add new Contact ----------------------------------------------------------------------
                if (selectedPhone == null) {
                    session.save(selectedContact);
                } else {
                    Contact contact = session.get(Contact.class, selectedContact.getId());
                    contact.addPhones(selectedContact.getPhones().toArray(new Phone[0]));
                }
                break;
            }
            case 3: { // UPDATE ----------------------------------------------------------------------------------------
                Contact contactToEdit = session.get(Contact.class, selectedContact.getId()); // отримання об'єкта за id
                if (!contactToEdit.getLastName().equals(selectedContact.getLastName())) {
                    contactToEdit.setLastName(selectedContact.getLastName());
                }
                if (!contactToEdit.getFirstName().equals(selectedContact.getFirstName())) {
                    contactToEdit.setFirstName(selectedContact.getFirstName());
                }
                int countPhones = contactToEdit.getPhones().size();
                for (int i = 0; i < countPhones; i++) {
                    if (!contactToEdit.getPhones().get(i).equals(selectedContact.getPhones().get(i))) {
                        Phone phone = contactToEdit.getPhones().get(i);
                        phone.setPhoneNumber(selectedContact.getPhones().get(i).getPhoneNumber());
                    }
                }
                break;
            }
            case 4: { // DELETE ----------------------------------------------------------------------------------------
                if (selectedPhone == null) {
                    // видалення контакта з каскадним видаленням усіх пов'язаних телефонів
                    Contact contactToDell = session.get(Contact.class, selectedContact.getId());
                    if (contactToDell != null) { // перевірка результата отримання об'єкта з БД - якщо об'єкт відсутній повернеться null
                        session.delete(contactToDell);
                    }
                } else {
                    // видалення телефона при наявності об'єкта Phone
                    Phone phoneToDell = session.get(Phone.class, selectedPhone.getId());
                    if (phoneToDell != null) {
                        session.delete(phoneToDell);
                    }
                }
                break;
            }
        }

        session.getTransaction().commit(); // закриття сесії - знімок - збереження в БД раніше зроблених змін
    }

}
