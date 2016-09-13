Start both services.   
In command line invoke commands:   
1. ```curl -XPOST -u demo:demo localhost:9090/oauth/token -d grant_type=password -d username=user -d password=user```      
2. ```curl -H 'Authorization: Bearer [token from step 1]' localhost:8090/entries/another-thread``` This will call callable with successful result.
3. ```curl -H 'Authorization: Bearer [token from step 1]' localhost:8090/entries/runnable``` This will call runnable with unsuccessful result.
