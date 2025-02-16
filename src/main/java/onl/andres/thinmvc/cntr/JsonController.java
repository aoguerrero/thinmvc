package onl.andres.thinmvc.cntr;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.thinmvc.excp.ServiceException;
import onl.andres.thinmvc.mdl.Response;
import onl.andres.thinmvc.utl.HttpUtils;

public abstract class JsonController<I, O> implements BaseController {

	private Class<I> inputType;
	private Gson gson;

	public JsonController(Class<I> type) {
		this.inputType = type;
		this.gson = new Gson();
	}

	public abstract O execute(I input);

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
		if(body.length == 0) 
			throw new ServiceException.BadRequest();
		I input = gson.fromJson(new String(body, StandardCharsets.UTF_8), inputType);
		String output = gson.toJson(execute(input));
		return new Response(HttpResponseStatus.OK, headers, output.getBytes());
	}
}
