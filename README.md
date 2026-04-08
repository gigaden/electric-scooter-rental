# [Схема базы данных](https://drawdb.vercel.app/editor/diagrams/6bba6286-2f69-446e-a9da-64612d8c4fbf)


# Read Me First
The following was discovered as part of building this project:

* The original package name 'ru.gigaden.electric-scooter-rental' is invalid and this project uses 'ru.gigaden.electric_scooter_rental' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.14-SNAPSHOT/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.14-SNAPSHOT/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.14-SNAPSHOT/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.14-SNAPSHOT/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [SpringDoc OpenAPI](https://springdoc.org/)
* [Liquibase Migration](https://docs.spring.io/spring-boot/3.5.14-SNAPSHOT/how-to/data-initialization.html#howto.data-initialization.migration-tool.liquibase)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi-demos/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

