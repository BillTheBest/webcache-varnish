sub vcl_pipe { 
	if (req.http.upgrade) { 
		set bereq.http.Upgrade = req.http.upgrade;
		return(pipe);
	}
	
	//pipes do not use keep-alive
	set bereq.http.Connection = "close";
	return(pipe);
}