callback-websocket
==================

[![Build Status](https://travis-ci.org/poolik/callback-websocket.svg?branch=master)](https://travis-ci.org/poolik/callback-websocket)
[![Coverage Status](https://coveralls.io/repos/poolik/callback-websocket/badge.png?branch=master)](https://coveralls.io/r/poolik/callback-websocket?branch=master)

A simple library to create request-response queries over java WebSockets API
# Why?

Time is ripe for using WebSockets in the wild. At least two latest versions of most common web browsers support them - http://caniuse.com/websockets.

Though WebSockets give you a fast full duplex communication channel, which is good for real-time data,
it doesn't give you a mechanism to create the classic request - response style queries. Because of this,
I guess currently the standard mechanism is to use a WebSocket for real-time notifications / messages (server -> client)
and standard AJAX requests for every other user action (client -> server).

The reasoning behind the callback-websocket library goes as this: 

If you already have a fast, full duplex communication channel, why not channel all of the required communication through it and **completely stop using AJAX**.

## Performance
If you completely stop making AJAX requests and marshal all of your requests through one fast (already open) socket, naturally you're it's going to increase the speed of your requests.

My testing shows that it **increases the speed of a request up to ~80%**
![Performance comparision](http://f.cl.ly/items/2O290L3A3C1Q3O3S3e1Z/performance_comparison_large.png)

But don't take my word for it, check out the [performance-comparison example](https://github.com/poolik/callback-websocket-examples/tree/master/performance-comparison) and run your own tests!

## Installation
1. Just add a dependency like so:

    ```xml
    <dependency>
        <groupId>com.poolik</groupId>
        <artifactId>callback-websocket</artifactId>
        <version>1.2</version>
    </dependency>
    ```
2. Download the [angular.websocket.callback](https://github.com/poolik/callback-websocket/blob/master/src/main/js/angular-websocket-callback.js?raw=true) module and add it as a dependency:

    ```javascript
    var app = angular.module('your-module', ['angular.websocket.callback']);
    ```
    
# Example
## AngularJS Client

To send classic requests over a WebSocket, inject the ```WebSocketService``` in your controller
and invoke one of it's methods:
* **.get("/url")** 
* **.post("/url", {data:"data"})**
* **.put("/url", {data:"data"})** 
* **.delete("/url", {data:"data"})** 

Each method returns a [promise](http://docs.angularjs.org/api/ng.$q), which will be either
resolved with the result of the request or rejected.

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

To receive any other data that might be pushed by the server to the client (some real-time notification) then use the
```.subscribe(callback);``` method. For example:

```javascript
noc.factory('NotificationListener', function ($rootScope, WebSocketService) {
    $rootScope.notifications = [];

    ... //connect the WebSocketService

    WebSocketService.subscribe(function (message) {
        $rootScope.notifications.push(message);
    });
});
```

## Java server

To handle requests on the server side you need to do two things:

1. In your WebSocketEndpoint @OnMessage method marshal the request to WebSocketRequestMarshaller like so:

    ```java
    @ServerEndpoint(value = "/websocket/")
    public class WebSocketEndpoint {
      private static final WebSocketRequestMarshaller marshaller = new WebSocketRequestMarshaller();
    
      @OnMessage
      public void onWebSocketText(final Session session, String message) {
        marshaller.handleRequest(session, message);
      }
    }
    ```

2. Implement a WebSocketRequestHandler 

    ```java
    public class HelloWorldHandler implements WebSocketRequestHandler {
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

All WebSocketRequestHandler-s are dynamically found using [Classfinder](https://github.com/poolik/classfinder), so there's no need to register anything anywhere.
To add a new handler simply create a class implementing ```WebSocketRequestHandler```.
    
Full stack examples can be found [here](https://github.com/poolik/callback-websocket-examples).
Hopefully you can get a better picture there, about how everything fits together.

# Filters

```WebSocketRequestMarshaller``` constructor also accepts a Collection of ```WebSocketFilter```-s.
```java
public interface WebSocketFilter {
  boolean accepts(WebSocketRequest request);
  boolean filter(WebSocketRequest request);
  Exception getError(String url);
}
```
With filters each request can be preprocessed to add authentication, logging or whatever else capabilities.
Out of the box an abstract ```UrlBasedFilter``` is available that filters request by the provided url regex.
Users are meant to subclass it and implement the ```boolean filter(WebSocketRequest request);``` method.

If the ```boolean filter(WebSocketRequest request);``` returns false
(ie. the filter rejects this request) then the error from ```Exception getError(String url);```
is returned to the client.

# Authentication

To implement authentication of requests an AuthenticationFilter is needed that,
for example, filters all requests to */api/**. To achieve this, from your login handler (on the server side)
respond with an object that among the authenticated user details sets a signed
[JSON Web Token](http://self-issued.info/docs/draft-ietf-oauth-json-web-token.html).
On the client side attach that token to the sessionStorage like so:

```javascript
$scope.login = function () {
    WebSocketService.post('/login', $scope.user).then(function (response) {
        $window.sessionStorage.token = response.token;
        $scope.message = 'Welcome';
      }, function (error) {
        delete $window.sessionStorage.token; // Erase the token if the user fails to log in
        $scope.message = 'Error: Invalid user or password';
      });
  };
```

The ```WebSocketService``` sets the *token* header on every request if some value exists in
```$window.sessionStorage.token```. So in your authentication filter for protected urls you can
simply retrieve the token via ```request.getHeaders().get("token")``` and verify it's signature
to make sure the user is authenticated.

More talk about JSON Web Token (JWT) based authentication can be found
[here](https://auth0.com/blog/2014/01/07/angularjs-authentication-with-cookies-vs-token/).
An example library to use in Java server side is
[nimbus-jose-jwt](http://connect2id.com/products/nimbus-jose-jwt) with code examples of how to
create JWT-s and verify their signature [here](http://connect2id.com/products/nimbus-jose-jwt/examples/jws-with-hmac)

Using the nimbus-jose-jwt library an AuthenticationFilter can look something like this
(using the HMAC protection):
```java
  @Override
  public boolean filter(WebSocketRequest request) {
    try {
      JWSObject jwsObject = JWSObject.parse(request.getHeaders().get("token"));
      return jwsObject.verify(new MACVerifier(JSON_WEB_TOKEN_SECRET));
    } catch (Exception e) {
      log.error("Failed to parse JSON web token", e);
      return false;
    }
  }
```
