package br.com.supermidia.security;

public class MessageResponseDTO {
	private String message;

	public MessageResponseDTO() {
	}

	public MessageResponseDTO(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public MessageResponseDTO setMessage(String message) {
		this.message = message;
		return this;
	}
}
