package fact.it.bikeservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(value = "bike")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Bike {
    private String id;
    private String skuCode;
    private String name;
    private String description;
    private Double price;
}
