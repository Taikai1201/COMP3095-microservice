package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.InventoryRequest;
import ca.gbc.orderservice.dto.InventoryResponse;
import ca.gbc.orderservice.dto.OrderLineItemDto;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.model.OrderLineItem;
import ca.gbc.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService{

    //                        Identifier, Type
//    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    @Value("${inventory.service.url}")
    private String inventoryApiUri;

    @Override
    public String placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItemList =
                orderRequest.getOrderLineItemDtoList()
                        .stream()
                        .map(this::mapToModel)
                        .toList();

        order.setOrderLineItemList(orderLineItemList);

        List<InventoryRequest> inventoryRequests = order.getOrderLineItemList()
                .stream().map(orderLineItem -> InventoryRequest
                        .builder()
                        .skuCode(orderLineItem.getSkuCode())
                        .quantity(orderLineItem.getQuantity())
                        .build()).toList();



        List<InventoryResponse> inventoryResponseList = webClientBuilder.build()
                .post()
                .uri(inventoryApiUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequests)
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block(); // block to make this synchronous

        assert inventoryResponseList != null;
        boolean allProductsInStock = inventoryResponseList
                .stream()
                .allMatch(InventoryResponse::isSufficientStock);

        if (Boolean.TRUE.equals(allProductsInStock)){
            orderRepository.save(order);

            // Lesson 7.1 - send event to kafka
            // Notification to Kafka
//            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));





            return "Order Placed Successfully";
        } else {
            throw new RuntimeException("Not all products are in stock");
        }
    }

    private OrderLineItem mapToModel(OrderLineItemDto orderLineItemDto){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }




}