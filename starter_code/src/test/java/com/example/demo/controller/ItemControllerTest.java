package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItemById() {
        Item expectedItem = new Item();
        expectedItem.setId(1L);
        expectedItem.setName("testItem");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(expectedItem));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item actualItem = response.getBody();
        assertNotNull(actualItem);
        assertEquals(expectedItem.getId(), actualItem.getId());
    }

    @Test
    public void testGetItemByName() {
        Item expectedItem = new Item();
        expectedItem.setId(1L);
        expectedItem.setName("testItem");
        when(itemRepository.findByName("testItem")).thenReturn(Collections.singletonList(expectedItem));
        ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> actualItems = response.getBody();
        assertNotNull(actualItems);
        assertEquals(1, actualItems.size());
        assertEquals(expectedItem.getId(), actualItems.get(0).getId());
    }

    @Test
    public void testGetItemByNameButNotFound() {
        when(itemRepository.findByName("testItem")).thenReturn(Collections.emptyList());
        ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");
        assertNotNull(response);
        assertTrue(CollectionUtils.isEmpty(response.getBody()));
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetAllItems() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("testItem1");
        Item item2 = new Item();
        item1.setId(2L);
        item1.setName("testItem2");
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> foundItems = response.getBody();
        assertNotNull(foundItems);
        assertEquals(2, foundItems.size());
    }
}
