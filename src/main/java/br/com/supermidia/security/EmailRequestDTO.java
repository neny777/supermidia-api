package br.com.supermidia.security;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailRequestDTO {
	@NotBlank
	@Email
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailRequestDTO other = (EmailRequestDTO) obj;
		return Objects.equals(email, other.email);
	}

	@Override
	public String toString() {
		return "EmailRequestDTO [email=" + email + "]";
	}
}
