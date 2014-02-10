'use strict';
angular.module('angular.websocket.callback', []).factory('WebSocketService', ['$q', '$rootScope', '$timeout', '$log', function($q, $rootScope, $timeout, $log) {

    var service = {};
    var ws = {};
    var requestCallbacks = {};
    var MAX_CONCURRENT_REQUESTS = 1000;
    var currentCallbackId = 0;
    var timeout = {};

    function ping () {
        ws.send("PING");
        timeout = $timeout(ping, 180000);
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
            timeout = $timeout(ping, 180000);
            if (service.openCallback) service.openCallback();
        };

        websocket.onerror = function() {
            if (service.errorCallback) service.errorCallback("Failed to open a connection with the server!" );
            $timeout.cancel(timeout);
        };

        websocket.onclose = function() {
            if (service.errorCallback) service.errorCallback("Connection lost with the server, refresh page to retry!");
            $timeout.cancel(timeout);
        };

        websocket.onmessage = function(message) {
            var data = JSON.parse(message.data);
            if (data.callbackId) listener(data);
            else service.callback(data);
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
                if (service.requestErrorCallback) service.requestErrorCallback(response.error);
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

    function sendRequest(type, url,  data) {
        if (typeof data !== "string") data = JSON.stringify(data);
        var defer = $q.defer();
        var callbackId = getCallbackId();
        var request = {
            type: type,
            url: url,
            data: data
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

    service.onSocketError = function(callback) {
        service.errorCallback = callback;
    };

    service.onRequestError = function(callback) {
        service.requestErrorCallback = callback;
    };

    service.onOpen = function(callback) {
        service.openCallback = callback;
    };

    return service;
}]);