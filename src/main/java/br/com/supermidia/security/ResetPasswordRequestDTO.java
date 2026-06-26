package br.com.supermidia.security;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
    private String newPassword;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, newPassword);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResetPasswordRequestDTO other = (ResetPasswordRequestDTO) obj;
		return Objects.equals(email, other.email) && Objects.equals(newPassword, other.newPassword);
	}

	@Override
	public String toString() {
		return "ResetPasswordRequestDTO [email=" + email + ", newPassword=" + newPassword + "]";
	}    
}
