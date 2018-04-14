# arch-playground-spring 

> "How can I die, when I have so much of the Universe left to explore?" - Stephen Hawking

Have fun hacking.

## Roadmap

* include font into service-worker

## Setup

> Use [IntelliJ](https://www.jetbrains.com/idea/download) for coding.

Simply open the cloned github folder and the gradle import wizard should pop up.

Install client dependencies:

    $ yarn install

## Production

Build client:

    $ yarn build

Build server:

    $ gradle build

Start server:

    $ java -jar build/libs/arch-playground-spring.jar

### Endpoints

* [Examples](http://localhost:4242/arch-playground-spring/examples)
* [Status](http://localhost:4242/arch-playground-spring/internal/status)
* [Loggers](http://localhost:4242/arch-playground-spring/internal/loggers)
* [Jobdefinitions](http://localhost:4242/arch-playground-spring/internal/jobdefinitions)
* [Jobs](http://localhost:4242/arch-playground-spring/internal/jobs)
* [Toggles](http://localhost:4242/arch-playground-spring/internal/toggles/console/index)
* [Health](http://localhost:4242/arch-playground-spring/internal/health)
* [Metrics](http://localhost:4242/arch-playground-spring/internal/metrics)
* [Httptrace](http://localhost:4242/arch-playground-spring/internal/httptrace)
* [Info](http://localhost:4242/arch-playground-spring/internal/info)
* [Mappings](http://localhost:4242/arch-playground-spring/internal/mappings)
* [Conditions](http://localhost:4242/arch-playground-spring/internal/conditions)
* [Env](http://localhost:4242/arch-playground-spring/internal/env)
* [Beans](http://localhost:4242/arch-playground-spring/internal/beans)

## Development

Start client watcher:

    $ yarn start

Start server:

    $ gradle bootRun [--debug-jvm]
    
You may need to set jvm args if using java 10

    $ --illegal-access=deny --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED

## Testing

### Test client

    $ yarn test

### Test server

    $ gradle test

### Test e2e

#### Test e2e on selenium grid

    $ yarn test:e2e
    
#### Test e2e locally

    $ yarn webdriver:install
    $ yarn webdriver:start
    $ yarn test:e2e:local

## Dependency Update

Client dependencies:

    $ yarn dependency-update

Server dependencies:

    $ gradle dependencyUpdates -Drevision=release
