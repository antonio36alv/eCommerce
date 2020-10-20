package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.AppUserRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private AppUserRepository userRepository = mock(AppUserRepository.class);

    private Authentication authentication = mock(Authentication.class);

    private UserOrder userOrder = mock(UserOrder.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void submitTest() {
        when(authentication.getName()).thenReturn("bobloblaw");
        when(userRepository.findByUsername("bobloblaw")).thenReturn(TestUtils.mockedUser());
//        when(userOrder.createFromCart(TestUtils.mockedCart())).thenReturn(TestUtils.mockedUserOrder());
        // test to make sure you can't submit an order under another user
        final ResponseEntity<UserOrder> wrongUserOrder = orderController.submit("notbobloblaw", authentication);
        // ensure we get back status code 403 - forbidden
        assertEquals(403, wrongUserOrder.getStatusCodeValue());
        // testing when submitting order under the correct user
//        final ResponseEntity<UserOrder> order = orderController.submit("bobloblaw", authentication);
//        assertEquals(200, order.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUserTest() {
        when(authentication.getName()).thenReturn("bobloblaw");
        when(userRepository.findByUsername("bobloblaw")).thenReturn(TestUtils.mockedUser());
        // test to make sure you can't submit an order under another user
        final ResponseEntity<List<UserOrder>> wrongUserOrderHistory = orderController.getOrdersForUser("notbobloblaw", authentication);
        // ensure we get back stAtus code 403 - forbidden
        assertEquals(403, wrongUserOrderHistory.getStatusCodeValue());
        final ResponseEntity<List<UserOrder>> orderHistory = orderController.getOrdersForUser("bobloblaw", authentication);
        assertEquals(200, orderHistory.getStatusCodeValue());
    }


}