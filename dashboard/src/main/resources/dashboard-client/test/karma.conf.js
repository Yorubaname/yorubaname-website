// Karma configuration
// http://karma-runner.github.io/0.12/config/configuration-file.html
// Generated on 2015-03-04 using
// generator-karma 0.9.0

module.exports = function(config) {
  'use strict';

  config.set({
    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // base path, that will be used to resolve files and exclude
    basePath: '../',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      // bower:js
      'bower_components/jquery/dist/jquery.js',
      'bower_components/angular/angular.js',
      'bower_components/bootstrap/dist/js/bootstrap.js',
      'bower_components/angular-animate/angular-animate.js',
      'bower_components/angular-resource/angular-resource.js',
      'bower_components/angular-route/angular-route.js',
      'bower_components/angular-sanitize/angular-sanitize.js',
      'bower_components/angular-touch/angular-touch.js',
      'bower_components/angular-ui-router/release/angular-ui-router.js',
      'bower_components/ng-tags-input/ng-tags-input.min.js',
      'bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
      'bower_components/angular-bootstrap-show-errors/src/showErrors.js',
      'bower_components/angular-ui-notification/dist/angular-ui-notification.min.js',
      'bower_components/papaparse/papaparse.js',
      'bower_components/ng-file-upload/ng-file-upload.js',
      'bower_components/ng-file-upload-shim/ng-file-upload-shim.js',
      'bower_components/angular-file-upload/angular-file-upload.min.js',
      'bower_components/angular-loader/angular-loader.js',
      'bower_components/angular-scenario/angular-scenario.js',
      'bower_components/ui-router/release/angular-ui-router.js',
      'bower_components/rangy/rangy-core.js',
      'bower_components/rangy/rangy-classapplier.js',
      'bower_components/rangy/rangy-highlighter.js',
      'bower_components/rangy/rangy-selectionsaverestore.js',
      'bower_components/rangy/rangy-serializer.js',
      'bower_components/rangy/rangy-textrange.js',
      'bower_components/textAngular/dist/textAngular.js',
      'bower_components/textAngular/dist/textAngular-sanitize.js',
      'bower_components/textAngular/dist/textAngularSetup.js',
      'bower_components/sifter/sifter.js',
      'bower_components/microplugin/src/microplugin.js',
      'bower_components/selectize/dist/js/selectize.js',
      'bower_components/footable/js/footable.js',
      'bower_components/d3/d3.js',
      'bower_components/c3/c3.js',
      'bower_components/autosize/dist/autosize.js',
      'bower_components/angular-breadcrumb/release/angular-breadcrumb.js',
      'bower_components/angular-retina/build/angular-retina.js',
      'bower_components/transitionize/dist/transitionize.js',
      'bower_components/fastclick/lib/fastclick.js',
      'bower_components/switchery/dist/switchery.js',
      'bower_components/ng-switchery/src/ng-switchery.js',
      'bower_components/multiselect/js/jquery.multi-select.js',
      'bower_components/select2/dist/js/select2.js',
      'bower_components/angular-cookies/angular-cookies.js',
      'bower_components/angular-toastr/dist/angular-toastr.tpls.js',
      'bower_components/angular-md5/angular-md5.js',
      'bower_components/angular-mocks/angular-mocks.js',
      // endbower
      'app/scripts/**/*.js',
      'test/mock/**/*.js',
      'test/spec/**/*.js'
    ],

    // list of files / patterns to exclude
    exclude: [
    ],

    // web server port
    port: 8080,

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: [
      'PhantomJS'
    ],

    // Which plugins to enable
    plugins: [
      'karma-phantomjs-launcher',
      'karma-jasmine'
    ],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false,

    colors: true,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,

    // Uncomment the following lines if you are using grunt's server to run the tests
    // proxies: {
    //   '/': 'http://localhost:9000/'
    // },
    // URL root prevent conflicts with the site root
    // urlRoot: '_karma_'
  });
};
