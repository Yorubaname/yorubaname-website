[![Build Status](https://travis-ci.org/Yorubaname/yorubaname-website.svg?branch=master)](https://travis-ci.org/Yorubaname/yorubaname-website)

## Yorubaname Website Application

The Yorubaname website application powers the backend services for www.yorubaname.com. It includes various modules that implements
the various needed functionality. The code for the front facing website found at www.yorubaname.com is also included in it.

## Getting Started With Running and Development

This should be your 5 minutes guide to getting the application running on your local machine and contributing code.

### Requirement

You need to have the following installed:
1. JDK 1.6+
2. MySQL
3. Maven

### Overview

First a quick overview of how things stack up should be of help.

The Yorubaname Website application is the backend portion of the yorubaname dictionary application, and it is built around individual modules that perform one functionality and that
functionality only. For example we have `tts-module` for tts, `elastic-search` module for the search etc.

The `webapi-module` is built using these other modules and it's own functionality is to expose the services the dictionary
application offers over a REST API.

The `website module` is the front facing part of the dictionary. It is also built using the API's exposed by the `webapi-module` 

The idea behind this set up is to be able to deploy the `webapi-module` and build services on its API the way the
yorubaname dashboard app (whose source can be found here https://github.com/Yorubaname/yorubaname-dashboard) does.

A mobile tier would also take advantage of this setup or to have the `webapi-module` as a coupled
dependency to another software component (The way the `website` component depends on `webapi-module`.)

Now with the general overview out of the way, let us look at getting the application running on your local machine.

### Running the Website Application

First create a MySQL database, with the following details:
Dictionary Name: dictionary
Username: dictionary
Password: dictionary

You have a couple of ways to run the core application.

#### Via an IDE
If you use an IDE like Intellij, you can run the application by running the `public static void main` in DictionaryApplication class

#### Via Spring Boot Run Plugin
1. cd into the projects parent directory and run `mvn clean install`. This would download all the projects dependencies, build the project and install it locally into the maven repository
2. cd into the website module ({parent_directory}/website) and run `mvn spring-boot:run`

The application would start up and can be accessed on port 8081.

### Running the Website Application In in-memory mode
It is also possible to run the application with the datastore being in-memory. 

The advantage is that, in that case, there won't be a need to install Mysql and the application can function without any external dependencies. 

The disadvantage is that whatever you add to the dictionary won't be durably persisted and would be lost on restart.
 
To start the application in in-memory mode, specify `inmemory` as the active profile when starting the application. For example:

`mvn spring-boot:run -Dspring.profiles.active=inmemory`

### Configuring ElasticSearch Properties

The ElasticSearch module does not require the installation of ElasticSearch as it will run with an embedded ElasticSearch instance: 
the only thing required is for the appropriate configurations be provided. which can be done in the application properties

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
by supplying the preferred directory through the `es.data.path` in the application.properties

### Running the Yorubaname Dashboard app

The Yorubaname Dashboard application powers the admin portal that can be used to manage entries in the yorubaname dictionary. It 
is a standalone application with a separate codebase.

The instructions on how to run the Yorubaname Dashboard Application can be found here https://github.com/Yorubaname/yorubaname-dashboard




