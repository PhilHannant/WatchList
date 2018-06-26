# WatchList

Implementation of a WatchList service using Akka Http

The Watchlist service should be implemented as an API. A single watchlist item is represented by a 5 digit
alphanumeric string (called a contentID) that is unique to a specific asset. The client teams will send
contentIDs to the new Watchlist service

Once the server is running the following curl commands can be used from a terminal supporting curl

Get - will return a watchlist containing contentIDs or an empty list if customer does not exist or has not contentIDs
curl -H "Content-type: application/json" -X GET -d '{"customerID": "abc"}' http://localhost:8080/customers
