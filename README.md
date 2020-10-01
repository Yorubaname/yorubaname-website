[![Build Status](https://travis-ci.org/Yorubaname/yorubaname-website.svg?branch=master)](https://travis-ci.org/Yorubaname/yorubaname-website)

## Your_name Website Application

The Yorubaname website application powers the backend services for www.yorubaname.com. It includes various modules that implement
the various needed functionality. The code for the front facing website found at www.yorubaname.com is also included in it.

## Getting Started With Running and Development

This should be your 5 minute guide to getting the application running on your local machine and contributing code.

### Requirement

You need to have the following installed:

1. JDK 1.6+
2. MySQL (you do not need to install MySQL if running in in-memory mode. Continue reading to learn how to start the application with in memory database)
3. Maven

Consult this [link](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html#CJAGAACB) for more information on how to install the JDK. 

Consult this [link](https://maven.apache.org/install.html) for more information on how to install Maven.

### Overview

First a quick overview of how things stack up should be of help.

The Yorubaname Website application is the backend portion of the yorubaname dictionary application, and it is built around individual modules that perform one functionality and that functionality only. For example we have `elastic-search` module for the search etc.

The `webapi-module` is built using these other modules and its own functionality is to expose the services the dictionary
application offers over a REST API.

The `website module` is the front facing part of the dictionary. It is also built using the APIs exposed by the `webapi-module` 

The idea behind this set up is to be able to deploy the `webapi-module` and build services on its API the way the
yorubaname dashboard app (whose source can be found here https://github.com/Yorubaname/yorubaname-dashboard) does.

A mobile tier would also take advantage of this setup or to have the `webapi-module` as a coupled
dependency to another software component (The way the `website` component depends on `webapi-module`.)

Now with the general overview out of the way, let us look at getting the application running on your local machine.

### Running the Website Application

There are two ways in which the application can be run for development:

* With a MySQL datastore
* With In-memory Database

Running the application with a MySQL database requires MySQL to be installed and running on the machine the 
application would be running on. The necessary database also needs to have been created. 
 
Running the application with an in-memory database does not require the installation and running of a database.

The next two sections describe how to start the application in the two outlined ways.

### Running the Website Application With a MySQL datastore

Feel free to skip this section and go to *Running the Website Application In in-memory mode* below if you do not 
intend to install/use MySQL.

Note, since the Yoruba Language makes extensive use of diacritics, your MySQL install needs to be configured to 
use UTF-8 in other to be able to handle the contents of the dictionary. If this is not done, the application won't 
start, because the bootstrap process inserts names which use diacritics into the database, and if the encoding is 
not right the insertiong would fail. In such a case you would see an error similar to:

```
Caused by: java.sql.SQLException: Incorrect string value: '\xE1\xBB\x8D\xCD\x81l...' for column 'extended_meaning' at row 1
	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:996)
````

Basically you would want to find your my.conf file and add the following piece of configuration:

```
[mysqld]
character-set-server=utf8
collation-server=utf8_general_c
```

You can consult the [Configuring the Character Set and Collation for Applications](https://dev.mysql
.com/doc/refman/en/charset-applications.html) section from the MySQL documentation for more details.


First create a MySQL database, with the following details:

* Dictionary Name: dictionary
* Username: dictionary
* Password: dictionary

In case you went ahead to install MySQL and created your database, without setting the character set as mentioned 
above, you can still alter your database to make use of the correct character set. To do that, run the following MySQL command:

```
ALTER DATABASE 'dictionary' DEFAULT CHARACTER SET = 'utf8' COLLATE 'utf8_unicode_ci';
```

Once the MySQL database has been created, and the encoding is set up all fine, you then have a couple of ways to start 
the core application.

#### Via an IDE

If you use an IDE like Intellij, you can run the application by running the `public static void main` in DictionaryApplication class

#### Via Spring Boot Run Plugin

1. cd into the projects parent directory and run `mvn clean install`. This would download all the project's dependencies, build the project and install it locally into the maven repository
2. cd into the website module ({parent_directory}/website) and run `mvn spring-boot:run`

The application would start up and can be accessed on port 8081.

Remember this command needs to be run from the website module, that is `{parent_directory}/website` directory.

### Running the Website Application In in-memory mode

It is also possible to run the application with the datastore being in-memory. In that case, there won't be a need to 
install MySQL and the application can function without any external dependencies. 

The disadvantage is that whatever you add to the dictionary won't be durably persisted and would be lost on restart.

#### Via an IDE

You can also start the application via an IDE when running with an in-memory database. If you use an IDE like 
Intellij, you can run the application by creating a `Run` configuration that runs the `public static void main` in 
`DictionaryApplication` class; you just need to make sure in the VM options, you pass `-Dspring.profiles
.active=inmemory` as the value
 
#### Via Spring Boot Run Plugin
 
To start the application in in-memory mode via the Spring boot run plugin, specify `inmemory` as the active profile 
when starting the application. For example:

`mvn spring-boot:run -Dspring.profiles.active=inmemory`

Remember this command needs to be run from the website module, that is `{parent_directory}/website` directory.

### Search functionality

The search API is defined in the `searchapi` module. We currently have two implementations for the search api:
1. ElasticSearch - Implemented in the `elasticsearch-module` module
2. JPA based search - Implemented in the `jpa-search-module` module

The `jpa-search-module` module is used in the `website` module which represents the website running at www.yorubaname.com 

If you want to use `elasticsearch` module then remove the following section in the pom.xml for `website` module:

```
    <dependency>
            <groupId>${project.groupId}</groupId>
            <artifactId>jpa-search-module</artifactId>
            <version>${project.version}</version>
    </dependency>
```

with

```
    <dependency>
            <groupId>${project.groupId}</groupId>
            <artifactId>elasticsearch-module</artifactId>
            <version>${project.version}</version>
    </dependency>
```

The `elasticsearch-module` needs to be configured. This is explained in the next session.

### Configuring ElasticSearch Properties

The ElasticSearch module does not require the installation of ElasticSearch as it will run with an embedded ElasticSearch instance: 
the only thing required is for the appropriate configurations to be provided which can be done in the application properties.

Different aspect of the embedded ElasticSearch be configured via application properties. The available configuration keys
and their defaults are

* es.clustername=yoruba_name_dictionary
* es.indexname=dictionary
* es.documenttype=nameentry
* es.hostname=localhost
* es.portnumber=9300
* es.data.path=

The embedded ElasticSearch module will by default create a "data" directory in the root directory where the application 
runs from and use this as the location to store data. This directory location can be configured to somewhere else 
by supplying the preferred directory through the `es.data.path` in the application.properties

### Exploring the REST API

The documentation for the API endpoints can be accessed by running the application and navigating to `http://localhost:8081/swagger-ui.html`

### Running the Yorubaname Dashboard app

The Yorubaname Dashboard application powers the admin portal that can be used to manage entries in the yorubaname dictionary. It 
is a standalone application with a separate codebase.

The instructions on how to run the Yorubaname Dashboard Application can be found here https://github.com/Yorubaname/yorubaname-dashboard




