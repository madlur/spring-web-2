package com.geekbrains.spring.web.carts.converters;

import com.geekbrains.spring.web.api.dto.CartDto;
import com.geekbrains.spring.web.carts.dto.Cart;
import org.springframework.stereotype.Component;

@Component
public class CartConverter {

    public CartDto cartToDto(Cart cart){
        return new CartDto(cart.getItems(), cart.getTotalPrice());
    }
}