package app.util;
import spark.*;

public class Filters {
	public static Filter addTrailingSlashes = (Request request, Response response) -> {
		if (!request.pathInfo().endsWith("/"))
			response.redirect(request.pathInfo() + "/");
	};
	// Enable GZIP for all responses
    public static Filter addGzipHeader = (Request request, Response response) -> {
        response.header("Content-Encoding", "gzip");
    };
}
