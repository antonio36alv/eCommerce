package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.AppUserRepository;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private AppUserRepository userRepository = mock(AppUserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private Authentication authentication = mock(Authentication.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCartTest() {
        // authenticate as bobloblaw
        when(authentication.getName()).thenReturn("bobloblaw");
        // find item by id one and use mock item from test utils (replicate of first item in data.sql)
        when(itemRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(TestUtils.mockedItem()));
        // when querying for bobloblaw return mocked user
        when(userRepository.findByUsername("bobloblaw")).thenReturn(TestUtils.mockedUser());

        // create a cart request under the wrong user
        final ResponseEntity<Cart> wrongUserCart = cartController.addTocart(createCartRequest("wrongname"), authentication);
        // check that we get a forbidden request
        assertEquals(403, wrongUserCart.getStatusCodeValue());

        // test item not found
        when(itemRepository.findById(Long.valueOf(1))).thenReturn(Optional.empty());
        final ResponseEntity<Cart> itemNotFoundResponse = cartController.addTocart(createCartRequest("bobloblaw"), authentication);
        assertEquals(404, itemNotFoundResponse.getStatusCodeValue());


        // mock userRepo not finding a user
        when(userRepository.findByUsername("bobloblaw")).thenReturn(null);
        final ResponseEntity<Cart> userNotFoundResponse = cartController.addTocart(createCartRequest("bobloblaw"), authentication);
        assertEquals(404, userNotFoundResponse.getStatusCodeValue());
    }

    @Test
    public void addToCartHappyPathTest() {
        when(authentication.getName()).thenReturn("bobloblaw");
        when(itemRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(TestUtils.mockedItem()));
        when(userRepository.findByUsername("bobloblaw")).thenReturn(TestUtils.mockedUser());
        when(cartRepository.save(TestUtils.mockedCart())).thenReturn(TestUtils.mockedCart());

        final ResponseEntity<Cart> cart = cartController.addTocart(createCartRequest("bobloblaw"), authentication);
        // cart request mock uses item id 1
        // item 1 price -> 2.99 * cart request quantity of 1 = price 2.99
        // will use item 1 as test case
        assertEquals(BigDecimal.valueOf(2.99), cart.getBody().getTotal());
        System.out.println(cart.getBody().getTotal());
        assertEquals(cart.getBody().getItems().get(0).getName(), TestUtils.mockedItem().getName());
        assertEquals(cart.getBody().getItems().get(0).getPrice(), TestUtils.mockedItem().getPrice());
        assertEquals(cart.getBody().getItems().get(0).getDescription(), TestUtils.mockedItem().getDescription());
    }

    @Test
    public void removeFromCartTest() {
        // test for non-matching request username/auth. username
        when(authentication.getName()).thenReturn("notbobloblaw");
        ResponseEntity<Cart> wrongUsernameResponse = cartController
                        .removeFromcart(createCartRequest("bobloblaw"), authentication);
        assertEquals(403, wrongUsernameResponse.getStatusCodeValue());

        when(userRepository.findByUsername("bobloblaw")).thenReturn(null);
        when(authentication.getName()).thenReturn("bobloblaw");
        ResponseEntity<Cart> nullUserResponse = cartController
                .removeFromcart(createCartRequest("bobloblaw"), authentication);
        assertEquals(404, nullUserResponse.getStatusCodeValue());

        when(userRepository.findByUsername("bobloblaw")).thenReturn(TestUtils.mockedUser());
        when(itemRepository.findById(Long.valueOf(1))).thenReturn(Optional.empty());
        ResponseEntity<Cart> nullItemResponse = cartController
                .removeFromcart(createCartRequest("bobloblaw"), authentication);
        assertEquals(404, nullItemResponse.getStatusCodeValue());


        when(userRepository.findByUsername("bobloblaw")).thenReturn(TestUtils.mockedUser());
        when(authentication.getName()).thenReturn("bobloblaw");
        when(itemRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(TestUtils.mockedItem()));
        ResponseEntity<Cart> happyPathResponse = cartController
                .removeFromcart(createCartRequest("bobloblaw"), authentication);
        assertEquals(200, happyPathResponse.getStatusCodeValue());

    }

    private static ModifyCartRequest createCartRequest(String username) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(username);
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);
        return cartRequest;
    }
}
