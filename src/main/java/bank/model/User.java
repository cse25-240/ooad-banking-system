package bank.model;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


/**A login for users of the system. The password isn't stored, rather its hash is.
 * a salted PBKDF2 hash is used(NFR-01). The password is never stored in memory, only the hash is. The password is cleared from memory after use(NFR-02)
 * After 3 failed login attempts, the user is locked out for 5 minutes(NFR-03). The password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character(NFR-04)
 * CUSTOMER role users are linked to exactly one customer record.
 */

public class User {

    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final Duration LOCKOUT_DURATION = Duration.ofMinutes(5);

    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;

    private final String userId;
    private final String username;
    private final String passwordHash; //base64 encoded hash(salt):base64 encoded salt(hash)
    private final Role role;
    private final String customerId; // Only for CUSTOMER role, null for ADMIN role
    private int failedLoginAttempts;
    private LocalDateTime lockoutEndTime;

    /**Aids in creating a new staff User instance.plain password that will be hashed */

    public User(String userId, String username, String plainPassword, Role role) {

       this (userId, username, plainPassword, role, null);
       
    }

/**Aids in creating a new customer user instance linked to a customer record */
public User(String userId, String username, String plainPassword, Role role, String customerId) {
     this(userId, username, hash(Employer.requireText(plainPassword, "password")), role,0,null, customerId);
}

/** used by dao to rebuilds stored user  */
public User(String userId, String username, String passwordHash, Role role, int failedLoginAttempts, LocalDateTime lockoutEndTime, String customerId) {
    
    this.userId = Employer.requireText(userId, "userId");
    this.username = Employer.requireText(username, "username");
    this.passwordHash = passwordHash;
    this.role = role;
    this.failedLoginAttempts = failedLoginAttempts;
    this.lockoutEndTime = lockoutEndTime;
    this.customerId = customerId; // can be null for ADMIN role
}

public boolean authenticate(String plainPassword) {
    if (plainPassword == null) {
        return false;
    }
    String[] parts = passwordHash.split(":");
    byte[] salt = Base64.getDecoder().decode(parts[0]);
    byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
    byte[] actualHash = pbkdf2(plainPassword, salt);
    return MessageDigest.isEqual(expectedHash, actualHash);

}

public boolean isLockedOut() {
    return isLockedOut(LocalDateTime.now());

}

public boolean isLockedOut(LocalDateTime now){
    return lockoutEndTime != null && now.isBefore(lockoutEndTime);
}

public void recordFailedLoginAttempt() {
    recordFailedLoginAttempt(LocalDateTime.now());
}


public void recordFailedLoginAttempt(LocalDateTime now) {
    failedLoginAttempts++;
    if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
        lockoutEndTime = LocalDateTime.now().plus(LOCKOUT_DURATION);
        failedLoginAttempts = 0; // reset failed attempts after lockout
    }
}
 
 public String getUserId() {
    
    return userId;
 }

 public String getUsername() {
    return username;
 }

 public String getPasswordHash() {
    return passwordHash;
 }

 public Role getRole() {
    return role;
 }


    public String getCustomerId() {
        return customerId;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public LocalDateTime getLockoutEndTime() {
        return lockoutEndTime;
    }

    private static String hash(String plainPassword) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(plainPassword, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }   

    private static byte[] pbkdf2(String Password, byte[] salt) {
        try { PBEKeySpec spec= new PBEKeySpec(Password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        }catch (GeneralSecurityException r){
            throw new IllegalStateException("Password Hashing Unavailable", r);
            
        }

            
        
        
    }


}