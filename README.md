# Overview
This is an evolution of a springblog post on microservices with spring.  The original source is
[here](https://spring.io/blog/2015/07/14/microservices-with-spring)

I ended up splitting up the original project that had three tiers (registration server, account micro service, and
the client webservice) into three separate projects.   This allowed me to inject the spring config service.
This requires the use of the bootstrap.yml to set the properties, and each app needs to be set separately.

This is the microservice piece of a multiproject demo.  At a minimum this will need to have the registration service
project and the AccountWebClient_Demo to demonstrate this microservice

# Account Service
This microservice provides various account related services, uses a H2 db that is populated on startup with 
some fake data. 

This is taken from the spring blog, and demonstrates (IMO) the intent of microservices:

When configuring applications with Spring we emphasize Loose Coupling and Tight Cohesion, These are not new 
concepts (Larry Constantine is credited with first defining these in the late 1960s - reference) but now we 
are applying them, not to interacting components (Spring Beans), but to interacting processes.

## Config
The spring boot application for this is the AccountsServer.java class.   This class has a @EnableDiscoveryClient
class.  It also sets a spring property called 'spring.config.name'.  This is used to register with the registration
service.  This service is either configured with an application.properties, or you can use the 'spring.config.name'.(yml,properties)
to provide configuration info.  There is an optional bootstrap.[yml,properties] which can be used to point to a spring
cloud config server.  It is loaded before the other properties files.

## Implementation details

The account service consists of:

* Rest endpoint with several account services.  This is just a typical @Controller in spring 3, or @RestController in
spring 4.
* A service class to encapuslate biz logic
* A spring data-jpa to access the database.
* H2 database bootstrapped with fake data, spring-data-jpa access (AccountRepository), configured by
AccountsWebApplication

The Accounts microservice provides a RESTful interface over HTTP, but any suitable protocol could be used. Messaging 
using AMQP or JMS is an obvious alternative.  A nice thing is that there is very little changes introduced by the
springboot microsercices, ie this is just a standardcan be converted 
into a Microservice.  Note that there can be multiple instances (docker apperas to be popular) of this service.

Once started, the [account service URL](http://localhost:2222) will show some default springboot links (details
below).

# Some interesting resources 
[Spring Cloud Netflix Tutorial](http://cloud.spring.io/spring-cloud-netflix/spring-cloud-netflix.html)
[Microservice Registraion and discovery with spring and eureka](https://spring.io/blog/2015/01/20/microservice-registration-and-discovery-with-spring-cloud-and-netflix-s-eureka)


# Spring Boot Web Default URL's
The account service app has a simple thymeleaf page [here](http://localhost:2222).  It contains a list of some default URL's 
that springboot webapps provide.  [this is a list](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)
of all of them (there are a lot, including these

1.  /beans - list all the spring beans in the context
2.  /env - list the environment values
3.  /health - list the health of the app.  there is a callback interface you can override to add custome behavior
4.  /metrics - various JVM stats
5.  /trace - Displays trace information (by default the last few HTTP requests).
6.  /info - Displays arbitrary application info.  I think this can be overridden
7.  /flyway - list flyway migrations (doesn't work, not sure why, probably needs a config or impl)
8.  /autoconfig - Displays an auto-configuration report showing all auto-configuration candidates and the reason why
they ‘were’ or ‘were not’ applied.
9.  /actuator - Provides a hypermedia-based “discovery page” for the other endpoints. Requires Spring HATEOAS to be on the classpath.

# Other things to look into

* Client side loadbalancing (Ribbon and others)
* Configuration Service
    * env vars (PAAS)
* Circuit Breaker
* HA config of services such as eureka, and experimenting with faults