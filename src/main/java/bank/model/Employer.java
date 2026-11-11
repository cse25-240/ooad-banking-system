package bank.model;

public class Employer{ 
    private final String name;
    private final String address;

  public Employer(String name, String address){
    this.name = requireText(name, "Employer's name");
    this.address = requireText(address, "Employer's address");
  }
static String requireText(String text, String fieldName) {
    if (text == null || text.isBlank()) {
        throw new IllegalArgumentException(fieldName+"is required");
    }
    return text.trim();
}
public String getName() {
    return name;
}
public String getAddress() {
    return address;
}
}