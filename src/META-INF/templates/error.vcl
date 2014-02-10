sub vcl_error {
	set obj.http.Content-Type = "text/html; charset=utf-8";
	
	if(obj.status == 701) {
		set obj.status = 200;
		synthetic "";
		return(deliver);
	}
	
	if(obj.status == 601) {
		set obj.http.Retry-After = "5";
		set obj.status = 400;
		synthetic "The request did not specify a valid virtual host.";
		return(deliver);
	}
}