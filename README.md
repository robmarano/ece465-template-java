# zkApp for ECE465

# Resources
* [Apache Zookeeper Explained: Tutorial, Use Cases and Zookeeper Java API Examples](http://java.globinch.com/enterprise-services/zookeeper/apache-zookeeper-explained-tutorial-cases-zookeeper-java-api-examples/)
* [Spring Boot - Uploading Files](https://spring.io/guides/gs/uploading-files/)

# Review of CAP Theorem
CAP Theorem (Brewer's theorem/ By Eric Brewer) states that, it is nearly impossible to achieve all three of Consistency, Availability and Partition tolerance. Any distributed system can choose only two of the following:
1. Consistency : All nodes is a distributed environment see the same data at the same time.
2. Availability: Guarantee that every request receives a response.
3. Partition Tolerance: Even if the connections between nodes are down, the other nodes works as guaranteed. (Tolerance to network partitions/broken communication)
   Zookeeper relaxes availability, but guarantees consistency and partition tolerance. Zookeeper implements an API that manipulates simple waitfree data objects organized hierarchically as in file systems.Pipelined architecture of ZooKeeper enables the execution of operations from a single client in FIFO order.











package ece465.zk.configuration;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Every Docket bean is picked up by the swagger-mvc framework - allowing for multiple swagger
     * groups i.e. same code base multiple swagger resource listings.
     */
    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/.*"))
                .paths(PathSelectors.regex("(?!/error).+"))
                .paths(PathSelectors.regex("(?!/updateFromLeader).+"))
                .build()
                .apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfo(
                "Sample distributed application",
                "Spring Boot REST API for Zookeeper demo!",
                "v1",
                "Terms of service",
                new Contact("Bikas Katwal", "", "bikas.katwal10@gmail.com"),
                "",
                "");
    }
}