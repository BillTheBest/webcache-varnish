sub vcl_hash {
	hash_data(req.http.Host);
	hash_data(regsuball(req.url, "([\?|&])_=[0-9]*&?", "jqNc")); //Ignore _=<serial> used by jQuery to simulate no cache
	
	//User Agent Device
	if(req.http.X-UA-Device) {
		hash_data(req.http.X-UA-Device);
	}
	
	//Paywall support
	hash_data(req.http.X-Paywall);
	
	//Cookie differentiation
	hash_data(req.http.X-Vary-Cookie);
	
	return (hash);
}