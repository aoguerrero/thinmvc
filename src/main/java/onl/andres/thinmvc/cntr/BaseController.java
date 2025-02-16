package onl.andres.thinmvc.cntr;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.thinmvc.mdl.Response;

public interface BaseController {
	
	public Response execute(HttpRequest request, byte[] body);

}
