package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "userRepository", userRepository);
    }

    @Test
    public void testAddToCartSuccessFully() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
        User user = new User();
        user.setUsername("testUser");
        user.setCart(new Cart());
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2.99));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(cart.getTotal(), BigDecimal.valueOf(2.99));
    }

    @Test
    public void testAddToCartFailedAsUsernameNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartFailedAsItemIdNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartSuccessFully() {
        Cart beforeCart = new Cart();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setPrice(BigDecimal.valueOf(2.99));
        beforeCart.addItem(item1);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setPrice(BigDecimal.valueOf(1.99));
        beforeCart.addItem(item2);
        assertEquals(2, beforeCart.getItems().size());
        assertEquals(BigDecimal.valueOf(4.98), beforeCart.getTotal());
        User user = new User();
        user.setUsername("testUser");
        user.setCart(beforeCart);
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart afterCart = response.getBody();
        assertNotNull(afterCart);
        assertEquals(1, afterCart.getItems().size());
        assertEquals(afterCart.getTotal(), BigDecimal.valueOf(1.99));
    }

    @Test
    public void testRemoveFromCartFailedUsernameNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartFailedItemIdNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(new User());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
