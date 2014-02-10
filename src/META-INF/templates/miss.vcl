sub vcl_miss {
    if (req.request == "DELETE") {
            purge;
            error 204 "Delete complete";
    }
}