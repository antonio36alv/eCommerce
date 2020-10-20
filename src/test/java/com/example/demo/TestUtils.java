package com.example.demo;

import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.UserOrder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if(!f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);

            if(wasPrivate) {
                f.setAccessible(false);
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected static AppUser mockedUser() {
        Cart cart = new Cart();
        cart.setId(Long.valueOf(1));
        AppUser appUser = new AppUser();
        appUser.setId(1);
        appUser.setUsername("bobloblaw");
        appUser.setCart(cart);
        return appUser;
    }

    public static Cart mockedCart() {
        Cart cart = new Cart();
        AppUser appUser = new AppUser();
        appUser.setId(1);
        appUser.setUsername("bobloblaw");
        cart.setId(Long.valueOf(1));
        cart.setItems(mockedItemList());
        cart.setUser(appUser);
        return cart;
    }

    public static Item mockedItem() {
        Item item = new Item();
        item.setId(Long.valueOf(1));
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        return item;
    }

    public static List<Item> mockedItemList() {
        List<Item> list = new ArrayList<>();
        list.add(mockedItem());
        return list;
    }

    public static UserOrder mockedUserOrder() {
        UserOrder uo = new UserOrder();
        uo.setId(Long.valueOf(1));
        uo.setItems(mockedItemList());
        uo.setTotal(BigDecimal.valueOf(2.99));
        uo.setUser(mockedUser());
        return uo;
    }
}
