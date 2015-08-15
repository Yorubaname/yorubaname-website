## Getting Started With Running and Development

This should be your 5 minutes guide to getting the application running on your local machine and contributing code.

### Requirement
1. JDK 1.6+
2. MySQL
3. ElasticSearch

### Overview

First a quick overview of how things stack up should help.

The dictionary application is built around individual modules that perform one functionality and that
functionality only. For example we have tts-module for tts, elastic-search module (//TODO change this to search module?)
for the search etc.

The webapi-module is built using these other modules and it's own functionality is to expose the services the dictionary
application offers over a REST API.

The dashboard is an example of an application that makes use of the REST API. (The dashboard is even more unique in the
sense it is a 100% stand alone JS application). The consumer facing website is another example of an application that
consumes the API exposed by the webapi-module.

The idea behind this set up is to be able to deploy the webapi-module and build services on its API the way the
dashboard app does (The mobile tier would also take advantage of this setup) or to have the webapi-module as a coupled
dependency to another software component (The way the website component depends on webapi-module.)

Hopefully this would not only allow us to easily build different components, but also makes it easier for others to, when
we decide to open source our efforts.

Now with the general overview out of the way, let us look at getting the application running on your local machine.

### Running the Website Application

First create a MySQL database, with the following details:
Dictionary Name: dictionary
Username: dictionary
Password: dictionary

You have a couple of ways to run the core application.

Via an IDE
1. If you use an IDE like Intellij, you can run the application by running the `public static void main` in DictionaryApplication class

Via Spring Boot Run Plugin
2. cd into the website module (/home/dadepo/Documents/fun/ydictionary/website) and run `mvn spring-boot:run`

The application would start up and can be accessed via 8081 port.


### Developing the Website Application (Aka the front end)

When you run the application using any of the two methods above (method 2 is advised if you do not want to bother with
the JAVA part, with its heavy IDE driven dev flow), you can find the front end assets in
`/home/dadepo/Documents/fun/ydictionary/website/src/main/resources/website`

The front end makes use of Handlerbar as it's templating technology. Changes you made to the templates are immediately
reflected and no need to restart (or recompile the application: the popular bane of Java development)

### Running the Core Application with ElasticSearch

The ElasticSearch module will run with an embedded ElasticSearch instance. So no need to have a standalone ElasticSearch
running in other to have the search functionality working.

Different aspect of the embedded ElasticSearch be configured via application properties. The available configuration keys
and their defaults are

es.clustername=yoruba_name_dictionary
es.indexname=dictionary
es.documenttype=nameentry
es.hostname=localhost
es.portnumber=9300
es.data.path=

The embedded ElasticSearch module will by default create a "data" directory in the root directory where the application 
runs from and use this as the location to store data. This directory location can be configured to somewhere else 
by supplying the preferred directory through the es.data.path= in the application.properties

### Running the dashboard app

You would find the dashbaord related information in README.md in the `dashboard` module


