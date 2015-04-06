# dashboardapp

## Dashboard Client App

The dashboard client application. It is a stand alone AngularJs that interacts with the backend services.

### Development

From the root directory run `grunt serve`. This would serve up the client app on port 9000. Make sure
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
 
The dashboard currently requires a login. To create a dev username and account follow these steps:

1. Start the main application. Either by running `mvn spring-boot:run` or via your IDE
2. Navigate to `localhost:8081/auth/createAdmin` This would create a dev account with the following credentials: <br/>
    a) username: admin@yorubaname.com <br/>
    b) password: admin <br/>
3. You should be able to login with these details.
4. If you logout and try loggin in and it gives an error, refresh the page. This is a known issue to be fixed.

//TODO auto create the admin account on start up of application


