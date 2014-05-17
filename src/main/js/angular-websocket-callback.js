'use strict';
angular.module('angular.websocket.callback', []).factory('WebSocketService', ['$q', '$rootScope', '$timeout', '$log', function($q, $rootScope, $timeout, $log) {

    var service = {};
    var ws = {};
    var requestCallbacks = {};
    var MAX_CONCURRENT_REQUESTS = 10000;
    var currentCallbackId = 0;
    var timeout = {};

    function ping () {
        ws.send("PING");
        timeout = $timeout(ping, 120000);
    }

    service.getWS = function(wsUrl) { //this method is only for testability
        return new WebSocket(wsUrl);
    };

    service.connect = function(wsUrl) {
        if (ws && ws.readyState == WebSocket.OPEN) {
            return false;
        }

        var websocket = service.getWS(wsUrl);

        websocket.onopen = function() {
            timeout = $timeout(ping, 120000);
            $rootScope.$broadcast('WebSocketService:onOpen');
        };

        websocket.onerror = function(event) {
            $log.error(event);
            $rootScope.$broadcast('WebSocketService:onError');
            $timeout.cancel(timeout);
        };

        websocket.onclose = function() {
            $log.info("Websocket closed!");
            $rootScope.$broadcast('WebSocketService:onClose');
            $timeout.cancel(timeout);
        };

        websocket.onmessage = function(message) {
            var data = JSON.parse(message.data);
            if (data.callbackId) listener(data);
            else $rootScope.$apply(service.callback(data));
        };

        ws = websocket;
        return true;
    };

    // Close the WebSocket connection
    service.disconnect = function() {
        ws.close();
    };

    function listener(response) {
        if(requestCallbacks.hasOwnProperty(response.callbackId)) {
            if (response.error) {
                $log.error(response.stacktrace);
                $rootScope.$broadcast('WebSocketService:requestError', response.error);
                $rootScope.$apply(requestCallbacks[response.callbackId].cb.reject(response.error));
            }
            else {
                $rootScope.$apply(requestCallbacks[response.callbackId].cb.resolve(JSON.parse(response.data)));
            }
            delete requestCallbacks[response.callbackId];
        }
    }

    service.post = function(url, data) {
        return sendRequest("POST", url, data);
    };

    service.getWithParams = function(url, params) {
        return sendRequest("GET", url, params);
    };

    service.get = function(url) {
        return sendRequest("GET", url, "");
    };

    service.delete = function(url, data) {
        return sendRequest("DELETE", url, data);
    };

    service.put = function(url, data) {
        return sendRequest("PUT", url, data);
    };

    service.sendBinary = function(binaryData) {
        if (webSocketNotOpen()) {
            return broadCastErrorAndGetRejectedPromise();
        } else ws.send(binaryData);
    };

    function broadCastErrorAndGetRejectedPromise() {
        var deferred = $q.defer();
        $rootScope.$broadcast('WebSocketService:requestError', "tried to send request when websocket is not open");
        deferred.reject("tried to send request when websocket is not open");
        return deferred.promise;
    }

    function webSocketNotOpen() {
        return !ws || ws.readyState != WebSocket.OPEN;
    }

    function getHeaders() {
        var headers = {};
        if ($window.sessionStorage.token) {
            headers.token = $window.sessionStorage.token;
        }
        return headers;
    }

    function sendRequest(type, url,  data) {
        if (webSocketNotOpen()) {
            return broadCastErrorAndGetRejectedPromise();
        }
        if (typeof data !== "string") data = JSON.stringify(data);
        var defer = $q.defer();
        var callbackId = getCallbackId();
        var request = {
            type: type,
            url: url,
            data: data,
            headers: getHeaders()
        };
        requestCallbacks[callbackId] = {
            time: new Date(),
            cb:defer
        };
        request.callbackId = callbackId;
        ws.send(JSON.stringify(request));
        return defer.promise;
    }

    function getCallbackId() {
        currentCallbackId += 1;
        if(currentCallbackId > MAX_CONCURRENT_REQUESTS) {
            currentCallbackId = 0;
        }
        return currentCallbackId;
    }

    service.subscribe = function(callback) {
        service.callback = callback;
    };

    return service;
}]);