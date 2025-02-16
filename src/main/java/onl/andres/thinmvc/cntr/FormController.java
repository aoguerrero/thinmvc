package onl.andres.thinmvc.cntr;

import java.util.Map;
import java.util.Optional;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.thinmvc.mdl.Response;
import onl.andres.thinmvc.utl.HttpUtils;

public abstract class FormController implements BaseController {

	protected final String path;
	private HttpHeaders responseHeaders;

	protected FormController(String path) {
		this.path = path;
	}

	public abstract Optional<String> execute(Map<String, String> formData);

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		this.responseHeaders = new DefaultHttpHeaders();
		var opId = execute(HttpUtils.bodyToForm(body));
		opId.ifPresentOrElse(id -> responseHeaders.add(HttpUtils.LOCATION, this.path.replace("{id}", id)),
				() -> responseHeaders.add(HttpUtils.LOCATION, this.path));
		return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[] {});
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

}
