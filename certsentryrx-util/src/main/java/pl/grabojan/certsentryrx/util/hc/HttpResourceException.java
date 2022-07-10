package pl.grabojan.certsentryrx.util.hc;

public class HttpResourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HttpResourceException() {
		super();
	}

	public HttpResourceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpResourceException(String message) {
		super(message);
	}

	public HttpResourceException(Throwable cause) {
		super(cause);
	}
	
}
