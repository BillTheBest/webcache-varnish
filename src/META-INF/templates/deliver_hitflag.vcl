sub vcl_deliver {
	remove resp.http.Surrogate-Control;
	remove resp.http.Content-Location;
	remove resp.http.Server;
	remove resp.http.Via;
	remove resp.http.X-Varnish;
	remove resp.http.x-url;
	
	if (obj.hits > 0) {
		set resp.http.X-Cache = "HIT";
	} else {
		set resp.http.X-Cache = "MISS";
	}
	
	return(deliver);
}