# Synchrolog Java Servlet Integration

Synchrolog Java Servlet Integration logs your web requests and  

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
compile 'com.synchrolog:synchrolog-java-servlet:1.0.0'
```

## Usage

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

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/synchrolog/synchrolog-java-servlet. 
This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to 
adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

The package is available as open source under the terms of 
the [MIT License](http://opensource.org/licenses/MIT).
