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
public class BikeResponse {
    private String id;
    private String skuCode;
    private String name;
    private String description;
    private Double price;
}