package fact.it.repairservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "repairs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String repairNumber;
    @OneToMany(cascade = CascadeType.ALL)
    private List<RepairLineItem> repairLineItemsList;
}
