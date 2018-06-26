# WatchList

Implementation of a WatchList service using Akka Http

The Watchlist service should be implemented as an API. A single watchlist item is represented by a 5 digit
alphanumeric string (called a contentID) that is unique to a specific asset. The client teams will send
contentIDs to the new Watchlist service

Once the server is running the following curl commands can be used from a terminal supporting curl

Get - will return a watchlist containing contentIDs or an empty list if customer does not exist or has not contentIDs
curl -H "Content-type: application/json" -X GET -d '{"customerID": "abc"}' http://localhost:8080/customers

Post - will add contentIDs to customer
curl -H "Content-type: application/json" -X POST -d '{"customerID": "123", "contentIDs":["zRE49", "wYqiZ", "15nW5", "srT5k", "FBSxr"]}' http://localhost:8080/customers

Delete - will remove listed contentID if present, if it does not exist customer's watchlist will remain untouched.
curl -H "Content-type: application/json" -X DELETE -d '{"customerID": "123", "contentID":"zRE49"}' http://localhost:8080/customers