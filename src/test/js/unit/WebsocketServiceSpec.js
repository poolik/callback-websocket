'use strict';

describe('WebSocketService', function () {
    beforeEach(function () {
        this.addMatchers({
            toEqualData: function (expected) {
                return angular.equals(this.actual, expected);
            }
        });
    });

    var webSocketService, webSocket, $timeout, $window;
    beforeEach(function () {
        module('angular.websocket.callback');

        inject(function ($injector, _$timeout_, _$window_) {
            $timeout = _$timeout_;
            $window = _$window_;
            webSocketService = $injector.get('WebSocketService');
        });
        webSocket = {
            readyState: WebSocket.OPEN,
            send: function () {},
            close: function () {}
        };
        spyOn(webSocket, 'send').andCallThrough();
        spyOn(webSocket, 'close').andCallThrough();
        spyOn(webSocketService, 'getWS').andReturn(webSocket);
    });

    function openSocket() {
        webSocketService.connect();
        webSocket.onopen();
        $timeout.flush();
        expect(webSocket.send).toHaveBeenCalledWith("PING");
    }

    describe('connection', function () {
        it('should connect only on first run', function () {
            expect(webSocketService.connect()).toBe(true);
            expect(webSocketService.connect()).toBe(false);
        });

        it('should call websocket close on disconnect', function () {
            webSocketService.connect();
            webSocketService.disconnect();
            expect(webSocket.close).toHaveBeenCalled();
        });
    });

    describe('ping', function () {
        it('should send ping message periodically', function () {
            openSocket();
            $timeout.flush();
            expect(webSocket.send).toHaveBeenCalledWith("PING");
        });

        it('should cancel periodic ping on error', function () {
            openSocket();
            webSocket.onerror();
            try {
                $timeout.flush();
            } catch (e) {
                expect(e.message).toBe('No deferred tasks to be flushed');
            }
        });

        it('should cancel periodic ping on error', function () {
            openSocket();
            webSocket.onclose();
            try {
                $timeout.flush();
            } catch (e) {
                expect(e.message).toBe('No deferred tasks to be flushed');
            }
        });
    });

    describe('callbacks', function () {
        it('should call subscribe callback when recieving data without callbackId', function () {
            var called = false;
            var calledWith = {};
            webSocketService.subscribe(function (data) {
                called = true;
                calledWith = data;
            });
            webSocketService.connect();
            webSocket.onmessage({data: '{"test":2}'});
            expect(called).toBe(true);
            expect(calledWith).toEqualData({test: 2});
        });

        it('should NOT call subscribe callback when recieving data WITH callbackId', function () {
            var called = false;
            var calledWith = {};
            webSocketService.subscribe(function (data) {
                called = true;
                calledWith = data;
            });
            webSocketService.connect();
            webSocket.onmessage({data: '{"test":2, "callbackId":1}'});
            expect(called).toBe(false);
            expect(calledWith).toEqualData({});
        });
    });

    describe('request', function () {
        beforeEach(function () {
            webSocketService.connect()
        });

        it('should attach token header if $window.sessionStorage.token is present', function () {
            $window.sessionStorage.token = "test";
            webSocketService.post("/test", {some: 'data'});
            expect(webSocket.send).toHaveBeenCalledWith('{"type":"POST","url":"/test","data":"{\\"some\\":\\"data\\"}","headers":{"token":"test"},"callbackId":1}');
            delete $window.sessionStorage.token;
        });

        it('should send and recieve POST requests', function () {
            var requestComplete = false;
            var responseComplete = 1;
            webSocketService.post("/test", {some: 'data'}).then(function (response) {
                requestComplete = true;
                responseComplete = response;
            });
            expect(webSocket.send).toHaveBeenCalledWith('{"type":"POST","url":"/test","data":"{\\"some\\":\\"data\\"}","headers":{},"callbackId":1}');
            webSocket.onmessage({data: '{"data":2, "callbackId":1}'});
            expect(requestComplete).toBe(true);
            expect(responseComplete).toBe(2);
        });

        it('should send and recieve GET requests with params', function () {
            var requestComplete = false;
            var responseComplete = 1;
            webSocketService.getWithParams("/test", {some: 'data'}).then(function (response) {
                requestComplete = true;
                responseComplete = response;
            });
            expect(webSocket.send).toHaveBeenCalledWith('{"type":"GET","url":"/test","data":"{\\"some\\":\\"data\\"}","headers":{},"callbackId":1}');
            webSocket.onmessage({data: '{"data":2, "callbackId":1}'});
            expect(requestComplete).toBe(true);
            expect(responseComplete).toBe(2);
        });

        it('should send and recieve GET requests without data', function () {
            var requestComplete = false;
            var responseComplete = 1;
            webSocketService.get("/test").then(function (response) {
                requestComplete = true;
                responseComplete = response;
            });
            expect(webSocket.send).toHaveBeenCalledWith('{"type":"GET","url":"/test","data":"","headers":{},"callbackId":1}');
            webSocket.onmessage({data: '{"data":2, "callbackId":1}'});
            expect(requestComplete).toBe(true);
            expect(responseComplete).toBe(2);
        });

        it('should send and recieve DELETE requests', function () {
            var requestComplete = false;
            var responseComplete = 1;
            webSocketService.delete("/test", {some: 'data'}).then(function (response) {
                requestComplete = true;
                responseComplete = response;
            });
            expect(webSocket.send).toHaveBeenCalledWith('{"type":"DELETE","url":"/test","data":"{\\"some\\":\\"data\\"}","headers":{},"callbackId":1}');
            webSocket.onmessage({data: '{"data":3, "callbackId":1}'});
            expect(requestComplete).toBe(true);
            expect(responseComplete).toBe(3);
        });

        it('should reject request promise when response contains error property', function () {
            var requestComplete = false;
            var errorResponse = {};
            webSocketService.get("/test").then(function () {
                requestComplete = true;
            }, function(error){
                errorResponse = error;
            });
            webSocket.onmessage({data: '{"error":"something bad!", "callbackId":1}'});
            expect(requestComplete).toBe(false);
            expect(errorResponse).toBe('something bad!');
        });
    })
});