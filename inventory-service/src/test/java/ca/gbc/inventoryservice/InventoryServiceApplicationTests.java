package ca.gbc.inventoryservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;
import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;
import ca.gbc.inventoryservice.service.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class InventoryServiceApplicationTests {

	@Mock
	private InventoryRepository inventoryRepository;

	@InjectMocks
	private InventoryServiceImpl inventoryService;

	@Test
	void isInStock() {
		// Arrange
		InventoryRequest request1 = new InventoryRequest("sku_12345", 3);
		InventoryRequest request2 = new InventoryRequest("sku_67899", 2);

		List<InventoryRequest> requests = Arrays.asList(request1, request2);

		Inventory inventory1 = new Inventory(1L, "sku_12345", 5);
		Inventory inventory2 = new Inventory(2L, "sku_67899", 1);

		when(inventoryRepository.findAllByInventoryRequest(any())).thenReturn(Arrays.asList(inventory1, inventory2));

		// Act
		List<InventoryResponse> responses = inventoryService.IsInStock(requests);

		// Assert
		assertEquals(2, responses.size());

		InventoryResponse response1 = responses.get(0);
		assertEquals("sku_12345", response1.getSkuCode());
		assertEquals(true, response1.isSufficientStock());

		InventoryResponse response2 = responses.get(1);
		assertEquals("sku_67899", response2.getSkuCode());
		assertEquals(false, response2.isSufficientStock());
	}

}
