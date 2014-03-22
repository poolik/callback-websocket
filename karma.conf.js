// Karma configuration
// Generated on Mon Feb 03 2014 15:20:50 GMT+0200 (EET)

module.exports = function(config) {
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: '.',

        preprocessors: {
            'src/main/js/*.js': ['coverage']
        },

        // frameworks to use
        frameworks: ['jasmine'],

        // list of files / patterns to load in the browser
        files: [
            'src/test/resources/angular.js',
            'src/test/resources/angular-mocks.js',
            'src/test/resources/jasmine.js',
            'src/main/js/angular-websocket-callback.js',
            'src/test/js/unit/**/*Spec.js'
        ],

        // list of files to exclude
        exclude: [
            'webapp/js/lib/angular.min.js',
            'webapp/js/lib/jasmine.js'
        ],

        // test results reporter to use
        reporters: ['progress', 'junit', 'coverage'],

        coverageReporter: {
            type : 'lcov',
            dir : 'coverage/'
        },

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,

        browsers: ['PhantomJS'],

        plugins : [
            'karma-junit-reporter',
            'karma-phantomjs-launcher',
            'karma-jasmine',
            'karma-coverage'
        ],

        // If browser does not capture in given duration [ms], kill it
        captureTimeout: 60000,

        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: false
    });
};