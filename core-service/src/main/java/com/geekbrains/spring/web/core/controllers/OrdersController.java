package com.geekbrains.spring.web.core.controllers;

import com.geekbrains.spring.web.api.dto.CartDto;
import com.geekbrains.spring.web.core.converters.OrderConverter;
import com.geekbrains.spring.web.core.dto.OrderDetailsDto;
import com.geekbrains.spring.web.core.dto.OrderDto;
import com.geekbrains.spring.web.core.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrderService orderService;
    private final OrderConverter orderConverter;
    private final RestTemplate restTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@RequestBody OrderDetailsDto orderDetailsDto, @RequestHeader String username){
        CartDto cartDto = restTemplate.getForObject("http://localhost:8080/web-market-cart/api/v1/cart?username={username}", CartDto.class, username);
        orderService.createOrder(orderDetailsDto, username, cartDto);
        restTemplate.getForObject("http://localhost:8080/web-market-cart/api/v1/cart/clear?username={username}", Void.class, username);
    }

    @GetMapping
    public List<OrderDto> getCurrentUserOrders(@RequestHeader String username) {
        return orderService.findOrdersByUsername(username).stream()
                .map(orderConverter::entityToDto).collect(Collectors.toList());
    }
}
