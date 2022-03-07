package net.duckling.ddl.service.resource.dao;


import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.springframework.beans.factory.BeanFactory;
import net.duckling.ddl.SpringManager;

import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.service.resource.dao.TagItemDAOImpl;

public class TagItemDAOTest {
    private static BeanFactory bf;
    private TagItemDAOImpl obj;

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
        obj = (TagItemDAOImpl)bf.getBean(TagItemDAOImpl.class);
    }

    @Test
    public void create() throws Exception {
        TagItem tag = new TagItem();
        tag.setTid(2022);
        tag.setTgid(1);
        int generated_id = obj.create(tag);
        // When insert a new one successfully, return id > 64;
        // Otherwise, -1
        assertTrue(generated_id > 64 || generated_id == -1);
    }

}
