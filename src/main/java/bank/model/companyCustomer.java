package bank.model;
import bank.exception.BankingException;

/**company customers are eligible to hold only savings and investment accounts */

public class companyCustomer extends Customer{

    private final String companyName;
    private final String registrationNumber;
    private final String contactOfficer;

    public companyCustomer(String customerId, String phoneNumber, String address, String companyName, 
                           String registrationNumber, String contactOfficer) {
        super(customerId, phoneNumber, address);
        this.companyName= Employer.requireText( companyName, "Company Name");
        this.registrationNumber= Employer.requireText( registrationNumber, "Registration Number");
        this.contactOfficer= Employer.requireText( contactOfficer, "Contact Officer");

                           }

    @Override
    public String getName() {
        return companyName;
    }

    @Override
    public String getCustomerType() {
        return "Company";
    }

    @Override
    public String getIdentificationNumber() {
        return registrationNumber;
    }

    @Override
    protected void checkEligibility(Account account)throws BankingException{
        if (account instanceof chequeAccount){
            throw new BankingException("A Company is not eligible to open a Cheque Account");
        }
    } 

    public String getCompanyName() {
        return companyName;
    }

    public String getRegistrationNumber(){
        return registrationNumber;
    }                  

    public String getContactOfficer() {
        return contactOfficer;
    }

}