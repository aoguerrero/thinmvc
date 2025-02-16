package onl.andres.thinmvc.excp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ServiceException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger(ServiceException.class);

	public static class BadRequest extends ServiceException {
		public BadRequest() {
			super();
		}

		public BadRequest(Throwable cause) {
			super(cause);
		}
	}

	public static class InternalServer extends ServiceException {
		public InternalServer() {
			super();
		}

		public InternalServer(Throwable cause) {
			super(cause);
		}
	}

	public static class NotFound extends ServiceException {
		public NotFound() {
			super();
		}

		public NotFound(Throwable cause) {
			super(cause);
		}
	}

	public static class Unauthorized extends ServiceException {
		public Unauthorized() {
			super();
		}

		public Unauthorized(Throwable cause) {
			super(cause);
		}
	}

	public ServiceException() {
		super();
	}

	public ServiceException(Throwable cause) {
		super(cause);
		logger.error("original-cause: ", cause);
	}
}
