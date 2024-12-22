package fact.it.repairservice;

import org.junit.jupiter.api.Test;

import fact.it.repairservice.dto.*;
import fact.it.repairservice.model.Repair;
import fact.it.repairservice.model.RepairLineItem;
import fact.it.repairservice.repository.RepairRepository;
import fact.it.repairservice.service.RepairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairServiceUnitTests {

    @InjectMocks
    private RepairService repairService;

    @Mock
    private RepairRepository repairRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(repairService, "bikeServiceBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(repairService, "reviewServiceBaseUrl", "http://localhost:8082");
    }

    @Test
    public void testPlaceRepair_Success() {
        // Arrange
        String skuCode = "sku1";
        Integer quantity = 2;
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "Test Description";
        String name = "Test Name";

        RepairRequest repairRequest = new RepairRequest();
        RepairLineItemDto repairLineItemDto = new RepairLineItemDto();
        repairLineItemDto.setId(1L);
        repairLineItemDto.setSkuCode(skuCode);
        repairLineItemDto.setQuantity(quantity);
        repairLineItemDto.setPrice(price); // Add price to the DTO
        repairRequest.setRepairLineItemsDtoList(Arrays.asList(repairLineItemDto));

        // ReviewResponse no longer contains skuCode
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setId("review1");
        reviewResponse.setComment("Great quality repair");
        reviewResponse.setRating(5);

        BikeResponse bikeResponse = new BikeResponse();
        bikeResponse.setSkuCode(skuCode);
        bikeResponse.setName(name);
        bikeResponse.setDescription(description);
        bikeResponse.setPrice(price);

        Repair repair = new Repair();
        repair.setId(1L);
        repair.setRepairNumber(UUID.randomUUID().toString());
        RepairLineItem repairLineItem = new RepairLineItem(1L, skuCode, price, description, quantity); // Correct constructor usage
        repair.setRepairLineItemsList(Arrays.asList(repairLineItem));

        // Mock the repository save method
        when(repairRepository.save(any(Repair.class))).thenReturn(repair);

        // Mock WebClient behavior
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ReviewResponse[].class)).thenReturn(Mono.just(new ReviewResponse[]{reviewResponse}));
        when(responseSpec.bodyToMono(BikeResponse[].class)).thenReturn(Mono.just(new BikeResponse[]{bikeResponse}));

        // Act
        boolean result = repairService.placeRepair(repairRequest);

        // Assert
        assertTrue(result);
        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    public void testPlaceRepair_FailureIfOutOfStock() {
        // Arrange
        String skuCode = "sku1";
        Integer quantity = 2;
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "Test Description";
        String name = "Test Name";

        RepairRequest repairRequest = new RepairRequest();
        RepairLineItemDto repairLineItemDto = new RepairLineItemDto();
        repairLineItemDto.setId(1L);
        repairLineItemDto.setSkuCode(skuCode);
        repairLineItemDto.setQuantity(quantity);
        repairLineItemDto.setPrice(price); // Add price to the DTO
        repairRequest.setRepairLineItemsDtoList(Arrays.asList(repairLineItemDto));

        // ReviewResponse no longer contains skuCode
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setId("review1");
        reviewResponse.setComment("Item out of stock");
        reviewResponse.setRating(1);

        BikeResponse bikeResponse = new BikeResponse();
        bikeResponse.setSkuCode(skuCode);
        bikeResponse.setName(name);
        bikeResponse.setDescription(description);
        bikeResponse.setPrice(price);

        // Mock WebClient behavior
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ReviewResponse[].class)).thenReturn(Mono.just(new ReviewResponse[]{reviewResponse}));

        // Act
        boolean result = repairService.placeRepair(repairRequest);

        // Assert
        assertFalse(result);
        verify(repairRepository, times(0)).save(any(Repair.class));
    }

    @Test
    public void testGetAllRepairs() {
        // Arrange
        RepairLineItem repairLineItem1 = new RepairLineItem(1L, "sku1", new BigDecimal("10.00"), "Description 1", 2);
        RepairLineItem repairLineItem2 = new RepairLineItem(2L, "sku2", new BigDecimal("20.00"), "Description 2", 3);

        Repair repair1 = new Repair(1L, "repair1", Arrays.asList(repairLineItem1, repairLineItem2));

        RepairLineItem repairLineItem3 = new RepairLineItem(3L, "sku3", new BigDecimal("30.00"), "Description 3", 4);
        RepairLineItem repairLineItem4 = new RepairLineItem(4L, "sku4", new BigDecimal("40.00"), "Description 4", 5);

        Repair repair2 = new Repair(2L, "repair2", Arrays.asList(repairLineItem3, repairLineItem4));

        when(repairRepository.findAll()).thenReturn(Arrays.asList(repair1, repair2));

        // Act
        List<RepairResponse> result = repairService.getAllRepairs();

        // Assert
        assertEquals(2, result.size());
        verify(repairRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateRepair() {
        // Arrange
        Long repairId = 1L;
        RepairRequest repairRequest = new RepairRequest();
        RepairLineItemDto repairLineItemDto = new RepairLineItemDto();
        repairLineItemDto.setSkuCode("sku1");
        repairLineItemDto.setQuantity(2);
        repairRequest.setRepairLineItemsDtoList(Arrays.asList(repairLineItemDto));

        Repair repair = new Repair(repairId, "repair1", Arrays.asList(new RepairLineItem(1L, "sku1", new BigDecimal("10.00"), "Description", 2)));

        when(repairRepository.findById(repairId)).thenReturn(java.util.Optional.of(repair));
        when(repairRepository.save(any(Repair.class))).thenReturn(repair);

        // Act
        repairService.updateRepair(repairId, repairRequest);

        // Assert
        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    public void testDeleteRepair() {
        // Arrange
        Long repairId = 1L;

        when(repairRepository.existsById(repairId)).thenReturn(true);

        // Act
        repairService.deleteRepair(repairId);

        // Assert
        verify(repairRepository, times(1)).deleteById(repairId);
    }

    @Test
    public void testDeleteRepair_NotFound() {
        // Arrange
        Long repairId = 1L;

        when(repairRepository.existsById(repairId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> repairService.deleteRepair(repairId));
    }
}
