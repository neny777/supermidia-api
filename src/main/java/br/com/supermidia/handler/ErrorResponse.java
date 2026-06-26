package br.com.supermidia.handler;

public class ErrorResponse {
    private String errorType;
    private String message;
    private int status;

    public ErrorResponse(String errorType, String message, int status) {
        this.errorType = errorType;
        this.message = message;
        this.status = status;
    }

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
