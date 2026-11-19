package bank.dto;

public record SessionDetails(String userId, String username, String role, String customerId) {

    public boolean staffMember() {
        return "TELLER".equals(role) || "ADMIN".equals(role);
    }

    public boolean isCustomer(){
        return "CUSTOMER".equals(role);
    }
    
}
