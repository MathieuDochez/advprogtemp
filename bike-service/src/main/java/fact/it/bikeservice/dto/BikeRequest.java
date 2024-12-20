package fact.it.bikeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BikeRequest {
    private String skuCode;
    private String name;
    private String description;
    private BigDecimal price;
}