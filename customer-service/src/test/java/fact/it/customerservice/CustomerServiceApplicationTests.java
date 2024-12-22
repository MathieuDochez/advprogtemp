package fact.it.customerservice;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceApplicationTests {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // Not needed with @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testCreateCustomer() {
        // Arrange
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name("John Doe")
                .build();
        Customer customer = Customer.builder()
                .name("John Doe")
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        customerService.createCustomer(customerRequest);

        // Assert
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testGetAllCustomers() {
        // Arrange
        Customer customer1 = Customer.builder().id("1").name("John Doe").build();
        Customer customer2 = Customer.builder().id("2").name("Jane Doe").build();
        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        // Act
        List<CustomerResponse> customerResponses = customerService.getAllCustomers();

        // Assert
        assertEquals(2, customerResponses.size());
        assertEquals("John Doe", customerResponses.get(0).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCustomersByName() {
        // Arrange
        Customer customer1 = Customer.builder().id("1").name("John Doe").build();
        Customer customer2 = Customer.builder().id("2").name("Jane Doe").build();
        when(customerRepository.findByNameIn(List.of("John Doe", "Jane Doe"))).thenReturn(List.of(customer1, customer2));

        // Act
        List<CustomerResponse> customerResponses = customerService.getAllCustomersByName(List.of("John Doe", "Jane Doe"));

        // Assert
        assertEquals(2, customerResponses.size());
        assertEquals("John Doe", customerResponses.get(0).getName());
        assertEquals("Jane Doe", customerResponses.get(1).getName());
        verify(customerRepository, times(1)).findByNameIn(List.of("John Doe", "Jane Doe"));
    }

    @Test
    void testUpdateCustomerSuccess() {
        // Arrange
        String id = "1";
        Customer existingCustomer = Customer.builder().id(id).name("John Doe").build();
        CustomerRequest updateRequest = CustomerRequest.builder().name("John Updated").build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

        // Act
        customerService.updateCustomer(id, updateRequest);

        // Assert
        assertEquals("John Updated", existingCustomer.getName());
        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void testUpdateCustomerNotFound() {
        // Arrange
        String id = "1";
        CustomerRequest updateRequest = CustomerRequest.builder().name("John Updated").build();

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.updateCustomer(id, updateRequest);
        });
        assertEquals("Customer with id 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, times(0)).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomerSuccess() {
        // Arrange
        String id = "1";
        when(customerRepository.existsById(id)).thenReturn(true);

        // Act
        customerService.deleteCustomer(id);

        // Assert
        verify(customerRepository, times(1)).existsById(id);
        verify(customerRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteCustomerNotFound() {
        // Arrange
        String id = "1";
        when(customerRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.deleteCustomer(id);
        });
        assertEquals("Customer with id 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).existsById(id);
        verify(customerRepository, times(0)).deleteById(anyString());
    }

}
