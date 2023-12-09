package ca.gbc.orderservice;

import ca.gbc.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import ca.gbc.orderservice.dto.InventoryResponse;
import ca.gbc.orderservice.dto.OrderLineItemDto;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.service.OrderServiceImpl;

import ca.gbc.orderservice.model.OrderLineItem;
import ca.gbc.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderServiceApplicationTests {

	@Mock
	private OrderRepository orderRepository;

	@MockBean
	private WebClient.Builder webClientBuilder;

	@InjectMocks
	private OrderServiceImpl orderService;

	@Test
	void placeOrder() {
		// Arrange
		OrderRequest orderRequest = new OrderRequest();
		OrderLineItemDto lineItemDto = new OrderLineItemDto();
		lineItemDto.setSkuCode("sku_12345");
		lineItemDto.setQuantity(2);
		lineItemDto.setPrice(new BigDecimal("10.0"));
		orderRequest.setOrderLineItemDtoList(Collections.singletonList(lineItemDto));

		// Mocking WebClient response for stock availability
		InventoryResponse stockAvailableResponse = new InventoryResponse();
		stockAvailableResponse.setSufficientStock(true);


		when(webClientBuilder.build().post()
				.uri(uriBuilder -> uriBuilder.path("${inventory.service.url}").build())
				.contentType(any())
				.bodyValue(any())
				.retrieve()
				.bodyToFlux(InventoryResponse.class)
				.collectList()
				.block())
				.thenReturn(Collections.singletonList(stockAvailableResponse));

		// Mocking repository save method for successful case
		when(orderRepository.save(any(Order.class))).thenReturn(new Order());

		// Act and Assert for stock availability
		orderService.placeOrder(orderRequest);
		Mockito.verify(orderRepository, Mockito.times(1)).save(any(Order.class));

		// Mocking WebClient response for stock shortage
		when(webClientBuilder.build().post()
				.uri(uriBuilder -> uriBuilder.path("${inventory.service.url}").build())
				.contentType(any())
				.bodyValue(any())
				.retrieve()
				.bodyToFlux(InventoryResponse.class)
				.collectList()
				.block())
				.thenReturn(Collections.singletonList((stockAvailableResponse)));

		// Act and Assert for stock shortage
		RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.placeOrder(orderRequest));
		Mockito.verify(orderRepository, Mockito.never()).save(any(Order.class));
		assertTrue(exception.getMessage().contains("Not all products are in stock, order cannot be placed"));
	}

}
