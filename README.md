# ece465-template-java

ECE 465 Source Code Template for demonstrating how to create API endpoints in Java

We will use REST as the communication methodology.

1. Create a new, simple Maven application using CLI. See [Maven documentation](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) on this method.

```bash
export MY_GROUP_ID=ece465.api
export MY_ARTIFACT_ID=my-api
mvn archetype:generate -DgroupId=${MY_GROUP_ID} -DartifactId=${MY_ARTIFACT_ID} -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

2. Follow these steps on [framework-less rest api](https://dev.to/piczmar_0/framework-less-rest-api-in-java-1jbl)
