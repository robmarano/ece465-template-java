# SNIPPETS

```java
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
```

```bash
function call_api() {
  # Get the API endpoint
  endpoint=$1

  # Get the request body
  body=$2

  # Make the request
  response=$(curl -X POST -H "Content-Type: application/json" -d "$body" "$endpoint")

  # Check the response status code
  if [[ $response -ne 200 ]]; then
    echo "Error: API call failed with status code $response"
    exit 1
  fi

  # Print the response body
  echo "Response body: $response"
}```