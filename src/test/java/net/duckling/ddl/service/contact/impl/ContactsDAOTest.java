package net.duckling.ddl.service.contact.impl;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import net.duckling.ddl.SpringManager;

import net.duckling.ddl.service.contact.Contact;

public class ContactsDAOTest {
    private static BeanFactory bf;
    private ContactsDAO obj;

    @BeforeClass
    public static void setUp() throws Exception {
        bf = SpringManager.getFactory();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Don't destroy factory if there are other Tests that would use it.
        // SpringManger.destroy();
    }

    @Before
    public void init() throws Exception {
        obj = (ContactsDAO)bf.getBean(ContactsDAO.class);
    }

    @Test
    public void create() throws Exception {
        List<Contact> contacts = obj.getUserContactsByName("admin@root.umt", "%com%");
        assertTrue(contacts.size() >= 0);
        contacts.forEach(System.out::println);
    }

}
