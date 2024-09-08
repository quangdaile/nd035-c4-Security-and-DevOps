package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmitFailedAsUsernameNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testSubmitSuccessfully() {
        User expectedUser = new User();
        expectedUser.setUsername("testUser");
        Cart expectedCart = new Cart();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setPrice(BigDecimal.valueOf(2.99));
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item2");
        item2.setPrice(BigDecimal.valueOf(1.99));
        expectedCart.addItem(item1);
        expectedCart.addItem(item2);
        expectedCart.setUser(expectedUser);
        expectedUser.setCart(expectedCart);
        when(userRepository.findByUsername("testUser")).thenReturn(expectedUser);
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(2, order.getItems().size());
        assertEquals(BigDecimal.valueOf(4.98), order.getTotal());
        assertNotNull(order.getUser());
        assertEquals(expectedUser.getUsername(), order.getUser().getUsername());
    }

    @Test
    public void testGetOrdersForUserFailedAsUsernameNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUserSuccessfully() {
        User expectedUser = new User();
        expectedUser.setUsername("testUser");
        Cart expectedCart = new Cart();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setPrice(BigDecimal.valueOf(2.99));
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item2");
        item2.setPrice(BigDecimal.valueOf(1.99));
        expectedCart.addItem(item1);
        expectedCart.addItem(item2);
        expectedCart.setUser(expectedUser);
        expectedUser.setCart(expectedCart);
        UserOrder order = UserOrder.createFromCart(expectedCart);
        when(userRepository.findByUsername("testUser")).thenReturn(expectedUser);
        when(orderRepository.findByUser(expectedUser)).thenReturn(Collections.singletonList(order));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertEquals(1, orders.size());
        UserOrder actualOrder = orders.get(0);
        assertEquals(2, actualOrder.getItems().size());
        assertEquals(BigDecimal.valueOf(4.98), actualOrder.getTotal());
        assertNotNull(actualOrder.getUser());
        assertEquals(expectedUser.getUsername(), actualOrder.getUser().getUsername());
    }
}
