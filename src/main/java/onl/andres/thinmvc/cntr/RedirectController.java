package onl.andres.thinmvc.cntr;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.thinmvc.mdl.Response;
import onl.andres.thinmvc.utl.HttpUtils;

public abstract class RedirectController implements BaseController {

	protected final String path;
	private HttpHeaders responseHeaders;

	protected RedirectController(String path) {
		this.path = path;
	}

	public abstract String execute(HttpRequest request);

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		this.responseHeaders = new DefaultHttpHeaders();
		String id = execute(request);
		if (id != null)
			responseHeaders.add(HttpUtils.LOCATION, this.path.replace("{id}", id));
		else
			responseHeaders.add(HttpUtils.LOCATION, this.path);
		return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[] {});
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

}
