package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemByIdTest() {
        when(itemRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(TestUtils.mockedItem()));
        final ResponseEntity<Item> item = itemController.getItemById(Long.valueOf(1));
        assertEquals(Long.valueOf(1), item.getBody().getId());
        assertEquals("Round Widget", item.getBody().getName());
        assertEquals("A widget that is round", item.getBody().getDescription());
        assertEquals(BigDecimal.valueOf(2.99), item.getBody().getPrice());
    }

    @Test
    public void getItemByNameTest() {
        when(itemRepository.findByName("Round Widget")).thenReturn(TestUtils.mockedItemList());
        final ResponseEntity<List<Item>> itemList = itemController.getItemsByName("Round Widget");
        Item item = itemList.getBody().get(0);
        assertEquals(Long.valueOf(1), item.getId());
        assertEquals("Round Widget", item.getName());
        assertEquals("A widget that is round", item.getDescription());
        assertEquals(BigDecimal.valueOf(2.99), item.getPrice());
    }

}
