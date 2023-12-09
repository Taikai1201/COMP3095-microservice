package ca.gbc.orderservice.controller;

import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.service.OrderServiceImpl;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
    @TimeLimiter(name = "inventory")
    public String placeOrder(@RequestBody OrderRequest orderRequest){
        log.info("Order Placed Successfully");
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";

    }

    public String placeOrderFallback(OrderRequest orderRequest, RuntimeException e){
        log.error("Exception is: {}", e.getMessage());
        return "FALLBACK INVOKED: Order Placed Failed. Please try again later.";
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
//    @TimeLimiter(name = "inventory")
//    @Retry(name = "inventory")
//    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest request){
//        orderService.placeOrder(request);
//        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(request));
//
//    }
//
//    public CompletableFuture<String> placeOrderFallback(OrderRequest request, RuntimeException e){
//        log.error("Exception is: {}", e.getMessage());
//        return CompletableFuture.supplyAsync(() -> "FALLBACK INVOKED: Order Placed Failed. Please try again later.");
//    }
}