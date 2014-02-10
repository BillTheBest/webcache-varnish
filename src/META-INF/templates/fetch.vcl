sub vcl_fetch {
	//Keeping all objects for 3 hours for stale responses (no backend available)
	set beresp.grace = 3h;
	
	//Header unification
	std.collect(beresp.http.Surrogate-Control);
	std.collect(beresp.http.Cache-Control);
	std.collect(beresp.http.Set-Cookie);
		
	//hack to allow ban from vcl_recv
	set beresp.http.X-URL = req.url;
    set beresp.http.X-Host = req.http.Host;
    set beresp.http.X-Group = req.http.X-Group;

	if(beresp.http.Content-Type ~ "(text|javascript)") {
		set beresp.do_gzip = true;
	}
	
	if(beresp.http.Surrogate-Control ~ "ESI") {
		set beresp.do_esi = true;
	}
	
	if(!beresp.http.Cache-Control) {
		set beresp.http.Cache-Control = "max-age=120";
	}
	
	if(beresp.status > 399 && beresp.status < 600) {
		set beresp.ttl = 30s;
		set beresp.http.Cache-Control = "max-age=30";
	}
	
	if(beresp.http.Surrogate-Control ~ "no-store") {
		set beresp.ttl = 0.2s;
		return(hit_for_pass);
	}
	
	//Saint Mode for 10 seconds
	if (beresp.status == 500) {
		set beresp.saintmode = 10s;
		return(restart);
	}
	
	return(deliver);
}