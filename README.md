callback-websocket
==================

A simple library to create request-response queries over java Websockets API
# Why?

Time is ripe for using websockets in the wild. At least two latest versions of most common web browsers support them - http://caniuse.com/websockets. 

Though websockets give you a fast full duplex communication channel, which is good for realtime data, it doesn't give you a mechanism to create the classic request -  response style queries. Because of this, I guess currently the standard mechanism is to use a websocket for realtime notifications / messages and standard AJAX requests for every other user action.

The reasoning behind the callback-websocket library goes as this: 

If you already have fast a full duplex communication channel, why not channel all of the required communication through it and **completely stop using AJAX**.

# Example
## AngularJS Client

To send classic requests over a websocket, inject the WebSocketService in your controller and invoke one of it's methods:
* **.get("/url")** 
* **.post("/url", {data:"data"})**
* **.put("/url", {data:"data"})** 
* **.delete("/url", {data:"data"})** 

Each method returns a [promise](http://docs.angularjs.org/api/ng.$q), which will be either resolved with the result of the request or rejected.

```javascript
var DemoController = function ($scope, WebSocketService) {
    WebSocketService.connect(getWSUrl());
    
    $scope.firstName = "";
    $scope.send = function() {
        WebSocketService.post("/helloworld", $scope.firstName).then(function(response) {
            $scope.response = response;
        }, function(error) {
            console.log("ERROR: " + error);
        })
    };
};
```

## Java server

To handle requests on the server side you need to do two things.

1. In your WebsocketEndpoint @OnMessage method marshal the request to WebsocketRequestHandler like so:

    ```java
    @ServerEndpoint(value = "/websocket/")
    public class WebsocketEndpoint {
    
      @OnMessage
      public void onWebSocketText(final Session session, String message) {
        WebsocketRequestHandler.handleRequest(session, message);
      }
    }
    ```

2. Implement a RequestHandler 

    ```java
    public class HelloWorldHandler implements RequestHandler {
      @Override
      public Pair<String, List<RequestType>> getRequestMappings() {
        return Pair.of("/helloworld", Arrays.asList(RequestType.POST));
      }
    
      @Override
      public WebsocketResponse handle(WebsocketRequest request) throws Exception {
        return new StringResponse("Hello " + request.getRequestBody());
      }
    }
    ```
    
Full stack examples can be found @ https://github.com/poolik/callback-websocket-examples
