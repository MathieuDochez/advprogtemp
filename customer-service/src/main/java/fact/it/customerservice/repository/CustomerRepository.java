package fact.it.customerservice.repository;

import fact.it.customerservice.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findByNameIn(List<String> name);
}
