# dashboardapp

This project is generated with [yo angular generator](https://github.com/yeoman/generator-angular)
version 0.11.1.

## Build & development

Run `grunt` for building and `grunt serve` for preview.

## Testing

Running `grunt test` will run the unit tests with karma.



== Dashboard Client App ==

The dashboard client application. It is a stand alone AngularJs that interacts with the backend services.

== Development ==

From the root directory run `grunt serve`. This would serve up the client app on port 9000. Make sure
the dictionary app is also running: that is having started it via your IDE or via mvn spring-boot:run


== Packaging ==

The dashboard application is built and packaged into the war that would be deployed. This is
handled by the dashboard build workflow.

This would see the dashboard app built and copied into /home/dadepo/Documents/fun/ydictionary/web/src/main/resources/public
