# Synchrolog Java Servlet Integration

Synchrolog Java Servlet Integration logs your web requests and can serve as a Log4j2
or Logback Appender. 

## Installation

This library is not available in Maven Central yet. Fortunately the library is 
trivial to build as there are few dependencies.

```bash
$ git clone git@github.com:synchrolog/synchrolog-java-servlet.git
$ cd synchrolog-java-servlet
$ mvn install 
```

After the library is available in the local Maven repo, you can include
the following dependency in your maven project:

```xml
<dependency>
    <groupId>com.synchrolog</groupId>
    <artifactId>synchrolog-java-servlet</artifactId>
    <version>0.0.2</version>
</dependency>
```

Or the following dependency in your gradle project:

```gradle
compile 'com.synchrolog:synchrolog-java-servlet:0.0.2'
```

## Basic Usage

Include the Synchrolog MiddlewareFilter in your web.xml configuration file.
This middleware filter will take care of logging all requests.

### Configuration Option A: Internal servlet config

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://java.sun.com/xml/ns/javaee"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
         http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">

    <filter>
        <filter-name>synchrolog</filter-name>
        <filter-class>com.synchrolog.servlet.MiddlewareFilter</filter-class>
        <init-param>
            <param-name>apiKey</param-name>
            <param-value>[PUT YOUR SYNCHROLOG API KEY HERE]</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>synchrolog</filter-name>
        <url-pattern>*</url-pattern>
    </filter-mapping>
</web-app>
```

That's it.

### Configuration Option B: Externalized config

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://java.sun.com/xml/ns/javaee"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
         http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">

    <filter>
        <filter-name>synchrolog</filter-name>
        <filter-class>com.synchrolog.servlet.MiddlewareFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>synchrolog</filter-name>
        <url-pattern>*</url-pattern>
    </filter-mapping>
</web-app>
```

Add -Dsynchrolog.apiKey=\[PUT YOUR SYNCHROLOG API KEY HERE\] to the java launch script

## Optional Advanced Usage: Logging Appenders

Synchrolog can be used as a Log4j2 or Logback appender

### Log4j2 Appender Config

Sample log4j2.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration xmlns="http://logging.apache.org/log4j/2.0/config" packages="com.synchrolog" status="WARN">
    <Appenders>
        <SynchrologAppender name="SYNCHROLOG"/>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="SYNCHROLOG" />
        </Root>
    </Loggers>
</Configuration>
```

### Logback Appender Config

Sample logback.xml

```xml
<configuration>
    <appender name="SYNCHROLOG" class="com.synchrolog.logger.SynchrologLogbackAppender">
        <!--<encoder>
            <pattern>%d [%thread] %-5level %logger - %msg%n</pattern>
        </encoder>-->
    </appender>
    <root level="INFO">
        <appender-ref ref="SYNCHROLOG"/>
    </root>
</configuration>
```

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/synchrolog/synchrolog-java-servlet. 
This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to 
adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

The package is available as open source under the terms of 
the [MIT License](http://opensource.org/licenses/MIT).
