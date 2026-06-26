package br.com.supermidia.security;

public class LoginResponseDTO {
	    
    private String token;
    private long expiresIn;

    public String getToken() {
        return token;
    }

    public LoginResponseDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public LoginResponseDTO setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
