package bank.dto;

public record CustomerSummary(String customerId, String customerType, String displayName,
                              String omang, String address, String phoneNumber, int accountCount) {

                                @Override 
                                public String toString(){
                                    return customerId + "-" + displayName + " (" + customerType + ")";
                                }
    
}
