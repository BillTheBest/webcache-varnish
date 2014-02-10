sub vcl_hit {
    if (req.request == "DELETE") {
            purge;
            error 204 "Delete complete";
    }
}