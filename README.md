## Getting Started With Running and Development

This should be your 5 minutes guide to getting the application running on your local machine and contributing code.

### Requirement
1. JDK 1.6+
2. MySQL

### Overview

First a quick overview of how things stack up should help.

The dictionary application is built around a core which provides a RESTful API endpoint. The core right now is found
in the *web-module*

All other aspects of the dictionary (dashboard, front end client, mobile client etc) would be clients to the RESTful api
provided by the core.

The core is a Java application built on Spring Boot. For persitence MySQL is currently used.

### Running the Core Application

First create a MySQL database, with the following details:
Dictionary Name: dictionary
Username: dictionary
Password: dictionary

You have a couple of ways to run the core application.

1. If you use an IDE like Intellij, you can run the application by running the `public static void main` in DictionaryApplication class
2. cd into the web module (/home/dadepo/Documents/fun/ydictionary/web) and run `mvn spring-boot:run`

The application would start up and can be accessed via 8081 port.

### Running the Core Application with ElasticSearch

The application's search functionality is powered by ElasticSearch, thus the application expects to have an ElasticSearch
cluster it can connect to.

To run the application and have the search functionality working you would have to download ElasticSearch and have it
running on your local machine. Follow the instructions on how to do this here: http://www.elastic.co/guide/en/elasticsearch/reference/1.4/setup.html

The only configuration needed is to update the cluster name to `yoruba_name_dictionary`. This you can do by editing the
ElasticSearch configuration file found in `/path/to/elasticsearc/elasticsearch/config/elasticsearch.yml` and edit the
`cluster.name` key to `yoruba_name_dictionary`

### Running the dashboard app

You would find the dashbaord related information in README.md in the `dashboard` module


