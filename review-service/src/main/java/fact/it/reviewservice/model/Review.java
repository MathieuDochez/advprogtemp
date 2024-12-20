package fact.it.reviewservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "review")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Review {
    private String id;
    private String comment;
    private Integer rating;
}
