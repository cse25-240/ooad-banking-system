package bank.model;

/**individual customers are eligible to hold all types of accounts */

public class individualCustomer extends Customer {
     
    private final String firstName;
    private final String lastName;
    private final String omang;

    public individualCustomer(String customerId, String phoneNumber, String address, 
                              String firstName, String LastName, String omang) {
                             super(customerId, phoneNumber, address);
                             this.firstName= Employer.requireText(firstName, "First Name");
                             this.lastName= Employer.requireText(LastName, "Last Name");
                             this.omang= Employer.requireText(omang, "National ID");
                              }

@Override
    public String getName() {
        return firstName+" "+lastName;
    }

    @Override
    public String getCustomerType() {
        return "Individual";
    }

    @Override
    public String getIdentificationNumber() {
        return omang;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNationalId() {
        return omang;
    }                          

}