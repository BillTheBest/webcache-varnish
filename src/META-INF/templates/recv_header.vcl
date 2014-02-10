	if (req.url ~ ".*/_oracle_http_server_webcache_static.*") {
		error 701 "Ok";
	}
	
	if (req.restarts == 0) {
		set req.http.ClientIP = client.ip;
	}
	
	call rbs_mobile;
	
	set req.http.X-Paywall = "none";
	set req.http.X-Vary-Cookie = "none";

	if((req.request == "DELETE" || req.request == "PURGE")) {
		if (!client.ip ~ allow_purge) {
			error 405 "Not allowed";
		}

        if(req.request == "PURGE") {
            if(req.http.X-Host) {
				ban("obj.http.X-Host ~ " + req.http.X-Host + " && obj.http.X-URL ~ " + req.http.X-Pattern + " && obj.http.X-Group ~ " + req.http.X-Group);
            } else {
				if(req.http.X-Group) {
					ban("obj.http.X-URL ~ " + req.http.X-Pattern + " && obj.http.X-Group ~ " + req.http.X-Group);
				} else {
					ban("obj.http.X-URL ~ " + req.http.X-Pattern);
				}
            }
            error 204 "Ban complete";
        }

        //Allow call purge
        return (lookup);
    }
	
	if (req.request != "POST" &&
		req.request != "GET" &&
		req.request != "HEAD") {
		error 405 "Not allowed";
		return(error);
	}
	