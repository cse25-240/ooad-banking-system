package bank.controller;


import bank.dto.SessionDetails;
import bank.exception.DeniedAccessException;

public class Session {

    private SessionDetails current;
    void begin(SessionDetails info){
        this.current = info;
    }

    public void logout(){
        this.current = null;
    }

    public boolean isLoggedIn(){
        return current != null;
    }

    public SessionDetails current(){
        return current;
    }

    public SessionDetails requireLogin() throws DeniedAccessException{
        if (current==null){
            throw new DeniedAccessException("Sign in first ");

        }
        return current;

    }

    public SessionDetails requireStaff() throws  DeniedAccessException{
    
        SessionDetails info = requireLogin();
        if (!info.staffMember()){
            throw new DeniedAccessException("Authorized for bank staff only");

        }
        return info;
}
public SessionDetails requireStaffOrOwner(String customerId) throws DeniedAccessException {
        SessionDetails info = requireLogin();
        if (info.staffMember()) {
            return info;
        }
        if (info.customerId() != null && info.customerId().equals(customerId)) {
            return info;
        }
        throw new DeniedAccessException("You can only see and change your own details.");
    }



}
