package com.geekbrains.spring.web.carts.controllers;

import com.geekbrains.spring.web.api.dto.CartDto;
import com.geekbrains.spring.web.api.dto.ProductDto;
import com.geekbrains.spring.web.api.dto.StringResponse;
import com.geekbrains.spring.web.carts.converters.CartConverter;
import com.geekbrains.spring.web.carts.dto.Cart;
import com.geekbrains.spring.web.carts.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartsController {
    private final CartService cartService;
    private final RestTemplate restTemplate;
    private final CartConverter cartConverter;

    @GetMapping
    public CartDto getCartForOrder (@RequestParam String username){
        String cartKey = cartService.getCartUuidFromSuffix(username);
        return cartConverter.cartToDto(cartService.getCurrentCart(cartKey));
    }
    @GetMapping("/clear")
    public void clearCart(@RequestParam String username){
        String cartKey = cartService.getCartUuidFromSuffix(username);
        cartService.clearCart(cartKey);
    }


    @GetMapping("/{uuid}")
    public Cart getCart(@RequestHeader(required = false) String username, @PathVariable String uuid) {
        return cartService.getCurrentCart(getCurrentCartUuid(username, uuid));
    }

    @GetMapping("/generate")
    public StringResponse getCart() {
        return new StringResponse(cartService.generateCartUuid());
    }

    @GetMapping("/{uuid}/add/{productId}")
    public void add(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
        ProductDto productDto = restTemplate.getForObject("http://localhost:8189/web-market-core/api/v1/products/" + productId, ProductDto.class);
        cartService.addToCart(getCurrentCartUuid(username, uuid), productDto);
    }

    @GetMapping("/{uuid}/decrement/{productId}")
    public void decrement(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
        cartService.decrementItem(getCurrentCartUuid(username, uuid), productId);
    }

    @GetMapping("/{uuid}/remove/{productId}")
    public void remove(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
        cartService.removeItemFromCart(getCurrentCartUuid(username, uuid), productId);
    }

    @GetMapping("/{uuid}/clear")
    public void clear(@RequestHeader(required = false) String username, @PathVariable String uuid) {
        cartService.clearCart(getCurrentCartUuid(username, uuid));
    }

    @GetMapping("/{uuid}/merge")
    public void merge(@RequestHeader(required = false) String username, @PathVariable String uuid) {
        cartService.merge(
                getCurrentCartUuid(username, null),
                getCurrentCartUuid(null, uuid)
        );
    }

    @GetMapping("/testCart")
    public String test() {
       return "Test cart OK";
    }

    private String getCurrentCartUuid(String username, String uuid) {
        if (username != null) {
            return cartService.getCartUuidFromSuffix(username);
        }
        return cartService.getCartUuidFromSuffix(uuid);
    }
}
