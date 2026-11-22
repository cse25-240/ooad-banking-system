package bank.dao.memory;
import bank.dao.CustomerDAO;
import bank.dao.AccessDataException;
import bank.model.Customer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCustomer implements CustomerDAO{

    private final Map<String, Customer> customers = new LinkedHashMap<>();

    @Override 
    public void save(Customer customer){
        if (customers.containsKey(customer.getCustomerId())){
            throw new AccessDataException("Customer: " + customer.getCustomerId() + " already exists");
        }
        customers.put(customer.getCustomerId(), customer);
    }

    @Override
    public Optional<Customer> findById(String customerId){
        return Optional.ofNullable(customers.get(customerId));
    }

    @Override 
    public Optional<Customer> findByIdentityNumber(String identityNumber){
        String required = identityNumber == null ? "" : identityNumber.trim();
        return customers.values().stream()
                .filter(cus -> cus.getIdentificationNumber().equalsIgnoreCase(required))
                .findFirst();
    }

    @Override 
    public List<Customer> findAll(){
        List<Customer> all = new ArrayList<>(customers.values());
        all.sort(Comparator.comparing(Customer::getCustomerId));
        return all;
    }

    @Override 
    public void updateDetails(Customer customer){
        if (!customers.containsKey(customer.getCustomerId())){
            throw new AccessDataException("Customer: " + customer.getCustomerId() + " does not exist");
        }
        customers.put(customer.getCustomerId(), customer);
    }

    @Override
    public void delete(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer != null && !customer.getAccounts().isEmpty()) {
            throw new AccessDataException("Cannot delete a customer who still has accounts.");
        }
        customers.remove(customerId);

}
}