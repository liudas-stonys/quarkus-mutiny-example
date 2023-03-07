package io.liudas;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DummyControllerTest {

  @Test
  void parallelAsyncHttpCallExampleTest() {
    RestAssured.get("/")
        .then()
        .statusCode(204);
  }
}
