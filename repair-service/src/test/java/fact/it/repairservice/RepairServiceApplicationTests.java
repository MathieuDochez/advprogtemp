package fact.it.repairservice;

import fact.it.repairservice.dto.*;
import fact.it.repairservice.model.Repair;
import fact.it.repairservice.model.RepairLineItem;
import fact.it.repairservice.repository.RepairRepository;
import fact.it.repairservice.service.RepairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepairServiceApplicationTests {

    @Mock
    private RepairRepository repairRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private RepairService repairService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlaceRepair_Success() {
        // Arrange
        RepairRequest repairRequest = new RepairRequest();
        RepairLineItemDto repairLineItemDto = new RepairLineItemDto(null, "sku123", BigDecimal.TEN, 2);
        repairRequest.setRepairLineItemsDtoList(List.of(repairLineItemDto));

        BikeResponse bikeResponse = new BikeResponse("sku123", "Bike1", "Desc1", BigDecimal.TEN);
        ReviewResponse reviewResponse = new ReviewResponse("sku123", "Great product", 5);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(), any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BikeResponse[].class)).thenReturn(Mono.just(new BikeResponse[]{bikeResponse}));
        when(responseSpec.bodyToMono(ReviewResponse[].class)).thenReturn(Mono.just(new ReviewResponse[]{reviewResponse}));
        when(repairRepository.save(any(Repair.class))).thenReturn(new Repair());

        // Act
        boolean result = repairService.placeRepair(repairRequest);

        // Assert
        assertTrue(result);
        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    void testGetAllRepairs() {
        // Arrange
        Repair repair = new Repair();
        repair.setRepairNumber("repair123");
        RepairLineItem repairLineItem = new RepairLineItem(null, "sku123", BigDecimal.TEN, 2);
        repair.setRepairLineItemsList(List.of(repairLineItem));

        when(repairRepository.findAll()).thenReturn(List.of(repair));

        // Act
        List<RepairResponse> responses = repairService.getAllRepairs();

        // Assert
        assertEquals(1, responses.size());
        assertEquals("repair123", responses.get(0).getRepairNumber());
        verify(repairRepository, times(1)).findAll();
    }

    @Test
    void testUpdateRepair_Success() {
        // Arrange
        Long repairId = 1L;
        RepairRequest repairRequest = new RepairRequest();
        RepairLineItemDto repairLineItemDto = new RepairLineItemDto(null, "sku123", BigDecimal.TEN, 2);
        repairRequest.setRepairLineItemsDtoList(List.of(repairLineItemDto));

        Repair existingRepair = new Repair();
        existingRepair.setRepairLineItemsList(new ArrayList<>());

        when(repairRepository.findById(repairId)).thenReturn(Optional.of(existingRepair));

        // Act
        repairService.updateRepair(repairId, repairRequest);

        // Assert
        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    void testUpdateRepair_NotFound() {
        // Arrange
        Long repairId = 1L;
        RepairRequest repairRequest = new RepairRequest();
        when(repairRepository.findById(repairId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> repairService.updateRepair(repairId, repairRequest));
    }

    @Test
    void testDeleteRepair_Success() {
        // Arrange
        Long repairId = 1L;
        when(repairRepository.existsById(repairId)).thenReturn(true);

        // Act
        repairService.deleteRepair(repairId);

        // Assert
        verify(repairRepository, times(1)).deleteById(repairId);
    }

    @Test
    void testDeleteRepair_NotFound() {
        // Arrange
        Long repairId = 1L;
        when(repairRepository.existsById(repairId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> repairService.deleteRepair(repairId));
    }

}
