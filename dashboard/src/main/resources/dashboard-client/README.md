# dashboardapp

## Dashboard Client App

The dashboard client application. It is a stand alone AngularJs that interacts with the backend services.

### Development

From the root directory of the dashboard app(eg /home/dadepo/Documents/fun/ydictionary/dashboard/src/main/resources/dashboard-client), 
run `grunt serve`. This would serve up the client app on port 9000. Make sure
the dictionary app is also running: that is having started it via your IDE or via 
mvn spring-boot:run from the web directory

#### Explicit steps for running dashboard in development
1. Check out the project and cd into the dashboard-client folder inside the dashboard module. 
That is `/ydictionary/dashboard/src/main/resources/dashboard-client`
2. Run `grunt serve`

Note: The content of the `Gruntfile.js` shows the other available grunt tasks.

### Packaging

The dashboard application is built and packaged into the war that would be deployed. This is
handled by the dashboard build workflow.

This would see the dashboard app built and copied into `/home/dadepo/Documents/fun/ydictionary/web/src/main/resources/public`

To run the maven build and include the dashboard app in the produced war, run with the `withDashboard` profile. That is:

`mvn package -PwithDashboard`

#### Explicit steps for running the Web App with the dashboard client included in the build process.

1. Check out the project and cd into the web folder inside the web-module dashboard. That is 
`/ydictionary/web`

2. Run `mvn spring-boot:run -PwithDashboard`

### Dashboard's Authorization

**Note: the authorization module is currently in an early stage of development and thus in a flux.**
 
The dashboard currently requires a login. A default dev admin account is created at start up of the main application:
username: admin@yorubaname.com
password: admin


