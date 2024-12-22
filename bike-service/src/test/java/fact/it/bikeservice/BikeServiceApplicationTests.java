package fact.it.bikeservice;

import fact.it.bikeservice.dto.BikeRequest;
import fact.it.bikeservice.dto.BikeResponse;
import fact.it.bikeservice.model.Bike;
import fact.it.bikeservice.repository.BikeRepository;
import fact.it.bikeservice.service.BikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikeServiceApplicationTests {

    @Mock
    private BikeRepository bikeRepository;

    @InjectMocks
    private BikeService bikeService;

    @BeforeEach
    void setUp() {
        // No need to manually initialize mocks when using @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testCreateBike() {
        // Arrange
        BikeRequest bikeRequest = BikeRequest.builder()
                .skuCode("SKU123")
                .name("Mountain Bike")
                .description("A fast mountain bike.")
                .price(299.99)
                .build();

        Bike bike = Bike.builder()
                .skuCode("SKU123")
                .name("Mountain Bike")
                .description("A fast mountain bike.")
                .price(299.99)
                .build();

        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);

        // Act
        bikeService.createBike(bikeRequest);

        // Assert
        verify(bikeRepository, times(1)).save(any(Bike.class));
    }

    @Test
    void testGetAllBikes() {
        // Arrange
        Bike bike1 = Bike.builder().id("1").skuCode("SKU123").name("Mountain Bike").description("A fast mountain bike.").price(299.99).build();
        Bike bike2 = Bike.builder().id("2").skuCode("SKU124").name("Road Bike").description("A smooth road bike.").price(199.99).build();
        when(bikeRepository.findAll()).thenReturn(List.of(bike1, bike2));

        // Act
        List<BikeResponse> bikeResponses = bikeService.getAllBikes();

        // Assert
        assertEquals(2, bikeResponses.size());
        assertEquals("Mountain Bike", bikeResponses.get(0).getName());
        assertEquals(299.99, bikeResponses.get(0).getPrice());
        verify(bikeRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBikesBySkuCode() {
        // Arrange
        Bike bike1 = Bike.builder().id("1").skuCode("SKU123").name("Mountain Bike").description("A fast mountain bike.").price(299.99).build();
        Bike bike2 = Bike.builder().id("2").skuCode("SKU124").name("Road Bike").description("A smooth road bike.").price(199.99).build();
        when(bikeRepository.findBySkuCodeIn(List.of("SKU123", "SKU124"))).thenReturn(List.of(bike1, bike2));

        // Act
        List<BikeResponse> bikeResponses = bikeService.getAllBikesBySkuCode(List.of("SKU123", "SKU124"));

        // Assert
        assertEquals(2, bikeResponses.size());
        assertEquals("Mountain Bike", bikeResponses.get(0).getName());
        verify(bikeRepository, times(1)).findBySkuCodeIn(List.of("SKU123", "SKU124"));
    }

    @Test
    void testUpdateBikeSuccess() {
        // Arrange
        String id = "1";
        Bike existingBike = Bike.builder().id(id).skuCode("SKU123").name("Mountain Bike").description("A fast mountain bike.").price(299.99).build();
        BikeRequest updateRequest = BikeRequest.builder()
                .skuCode("SKU123")
                .name("Updated Mountain Bike")
                .description("A better mountain bike.")
                .price(350.00)
                .build();

        when(bikeRepository.findById(id)).thenReturn(Optional.of(existingBike));
        when(bikeRepository.save(existingBike)).thenReturn(existingBike);

        // Act
        bikeService.updateBike(id, updateRequest);

        // Assert
        assertEquals("Updated Mountain Bike", existingBike.getName());
        assertEquals(350.00, existingBike.getPrice());
        verify(bikeRepository, times(1)).findById(id);
        verify(bikeRepository, times(1)).save(existingBike);
    }

    @Test
    void testUpdateBikeNotFound() {
        // Arrange
        String id = "1";
        BikeRequest updateRequest = BikeRequest.builder()
                .skuCode("SKU123")
                .name("Updated Mountain Bike")
                .description("A better mountain bike.")
                .price(350.00)
                .build();

        when(bikeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bikeService.updateBike(id, updateRequest);
        });
        assertEquals("Bike with id 1 not found", exception.getMessage());
        verify(bikeRepository, times(1)).findById(id);
        verify(bikeRepository, times(0)).save(any(Bike.class));
    }

    @Test
    void testDeleteBikeSuccess() {
        // Arrange
        String id = "1";
        when(bikeRepository.existsById(id)).thenReturn(true);

        // Act
        bikeService.deleteBike(id);

        // Assert
        verify(bikeRepository, times(1)).existsById(id);
        verify(bikeRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteBikeNotFound() {
        // Arrange
        String id = "1";
        when(bikeRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bikeService.deleteBike(id);
        });
        assertEquals("Bike with id 1 not found", exception.getMessage());
        verify(bikeRepository, times(1)).existsById(id);
        verify(bikeRepository, times(0)).deleteById(anyString());
    }

}
