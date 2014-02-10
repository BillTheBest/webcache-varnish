		if(req.url ~ "/css/estilos-site-legado.css") {
			return(pipe);
		}
		
		if(req.http.Cookie ~ "clicRBSv2.skin.rs") {
			set req.http.X-Paywall = regsub(req.http.Cookie, ".*(?:clicRBSv2.skin.rs=([^;]+)).*", "\1");
			set req.hash_always_miss = true;
		}