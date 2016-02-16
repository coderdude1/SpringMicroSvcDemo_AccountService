# Overview
This is an implementation of the spring microservices tutorial, rather than just cloning and running it I typed
it in so I can understand what is going on rather than just reading spring blog entry.  Source is [here](https://spring.io/blog/2015/07/14/microservices-with-spring)

This is a split up version of that (I took the 3 components in the original project and split them into seperate
projects, so I could experiment with using the spring config service.  this requires the use of the bootstrap.yml
to set the properties, and each app needs to be set separately.

# Account-Service
uses a H2 db that is populated on startup with some fake data.

# Spring Boot Web Default URL's
go to [url list](http://localhost:2222).  [this is a list](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)
of all of them (there are a lot, including these

1 /beans - list all the spring beans in the context
2 /env - list the environment values
3 /health - list the health of the app.  there is a callback interface you can override to add custome behavior
4 /metrics - various JVM stats
5 /trace - Displays trace information (by default the last few HTTP requests).
6 /info - Displays arbitrary application info.  I think this can be overridden
7 /flyway - list flyway migrations (doesn't work, not sure why, probably needs a config or impl)
8 /autoconfig - Displays an auto-configuration report showing all auto-configuration candidates and the reason why they ‘were’ or ‘were not’ applied.
9 /actuator - Provides a hypermedia-based “discovery page” for the other endpoints. Requires Spring HATEOAS to be on the classpath.

# Eureka Registration/Discovery service
This is the registry (opensourced by netflix) that spring microservices uses.  Services can register and lookup other
services here.  The generic name is 'Discovery Server' since there are other implementations besides Eurkea.  There are
netflix specific annotations and spring-cloud generic impls,  when using the generic it will load whatever impl is on the
classpath.  In our case in the pom there is a dependency called  spring-cloud-starter-eureka-server which specifies
the netflix stuff

This is very similar to the rmi-registry, or the corba service lookup.  Spring boot makes this very
trivial to create.  Registered services are availble to clients, who will use a generic service name to request a service.
this server will then provide an actual url.  Note that there are ways to do VIP type loadbalancing, both client and
server.  I don't get into that yet.  There are other tools that netflix (and I guess others) provide such as circuit-breaker
loadbalancing, disovery service, etc

It is configured via the registration-server.yml file.  There are other ways to do this too.

After starting it, http://localhost:1111 will show info about the discovery service, including what services are
registered to the disovery server.  The discovery service includes an automatic heartbeat, so if a service drops off
it will be cleared from the Discovery service one it fails the heartbeat check.

Show  JSON status of all apps
http://localhost:1111/eureka/apps/

The class to run is io.pivotal.microservices.services.registration.RegistrationServer.  It uses SPring Boot

# Account Service
Taken from the spring docs:

When configuring applications with Spring we emphasize Loose Coupling and Tight Cohesion, These are not new 
concepts (Larry Constantine is credited with first defining these in the late 1960s - reference) but now we 
are applying them, not to interacting components (Spring Beans), but to interacting processes.

The account service consists of:

* Rest endpoint with several account services.  This is just a typical @Controller in spring 3, or @RestController in
spring 4.
* A service class to encapuslate biz logic
* A spring data-jpa to access the database.
* H2 database bootstrapped with fake data, spring-data-jpa access (AccountRepository), configured by
AccountsWebApplication

The spring boot application for this is the AccountsServer.java class.   This class has a @EnableDiscoveryClient class,
 and It looks like the convention is to look for a yml file that contains the configuration too look for.  The name of the
 file is similar to the spring boot class, ie accounts-server.yml.  This annotation causes spring boot to lookup a 
 discovery service and register itself.

The Accounts microservice provides a RESTful interface over HTTP, but any suitable protocol could be used. Messaging 
using AMQP or JMS is an obvious alternative.  Note that it looks like any rest service can be converted 
into a Microservice.  Note that there can be multiple instances (docker apperas to be popular) of this service.

Spring boot provides some other interesting endpoints (configurable) that show some insite into the springboot app.

For the [account service](http://localhost:2222) will show some of them, including envrionment, trace calls etc.

# Webservice 
This is a rest interface/Thymeleaf app that will be a client to the Accounts microservice.

## Some interesting stuff
The rest template that is used to access the Account microservice has some cool stuff going on.  The @EnableDiscoveryClient
will recognize the REstEmplate being autowired and injects the microservice connection instead (ie it talks to the DisvoeryServer
and gets the url (not sure if it does it fore each request, or caches it.)

[This is the url to see the webserver stuff](http://localhost:3333/) it includes links for demoing the microservice calls
and the beans that provide various metrics and such

# Some spring boot stuff
Spring enables certain default endpoings such as  /info and /health that I think can be populated with app specific stuff via
a callback handler.


# Some interesting resources 
[Spring Cloud Netflix Tutorial](http://cloud.spring.io/spring-cloud-netflix/spring-cloud-netflix.html)
[Microservice Registraion and discovery with spring and eureka](https://spring.io/blog/2015/01/20/microservice-registration-and-discovery-with-spring-cloud-and-netflix-s-eureka)

# Things to look into

* Client side loadbalancing (Ribbon and others)
* Configuration Service
    * env vars (PAAS)
    * actual config service (cloud foundry and spring cloud).  Not sure how the client knows where to talk to, DNS?
* Circuit Breaker
* HA config of services such as eureka