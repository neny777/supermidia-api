package br.com.supermidia.security;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CodeValidationRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeValidationRequestDTO other = (CodeValidationRequestDTO) obj;
		return Objects.equals(code, other.code) && Objects.equals(email, other.email);
	}

	@Override
	public String toString() {
		return "CodeValidationRequestDTO [email=" + email + ", code=" + code + "]";
	}    
}
