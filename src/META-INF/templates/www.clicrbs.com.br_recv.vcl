		if(req.url ~ "^/(ac|al|ap|am|ba|ce|df|es|go|ma|mt|ms|mg|pa|pb|pr|pe|pi|rj|rn|rs|ro|rr|sc|sp|se|to)/$") {
			set req.http.X-Vary-Cookie = regsub(req.http.Cookie, ".*(?:clicRBSv2\.skin=([^;]+)).*", "\1");
		} else {		
			if(req.url ~ "^/zerohora/jsp/default2.jsp.*") {
				set req.http.X-Vary-Cookie = regsub(req.http.Cookie, ".*(?:zhoff_user=([^;]+)).*", "\1");
			} else {		
				if(req.url ~ "^/diariocatarinense/jsp/default2.jsp.*") {
					set req.http.X-Vary-Cookie = regsub(req.http.Cookie, ".*(?:dcoff_user=([^;]+)).*", "\1");
				} else {		
					if(req.url ~ "^/jornais/anoticia/jsp/default2.jsp.*") {
						set req.http.X-Vary-Cookie = regsub(req.http.Cookie, ".*(?:anoff_user=([^;]+)).*", "\1");
					}
				}
			}
		}