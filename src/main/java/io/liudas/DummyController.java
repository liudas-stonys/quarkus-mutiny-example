package io.liudas;

import io.smallrye.mutiny.Uni;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class DummyController {

  private static final Logger log = Logger.getLogger("DummyController");
  private final HttpClient httpClient =
      HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
  private final ExecutorService executorService = Executors.newFixedThreadPool(4);

  @GET
  public void parallelAsyncHttpCallExample() {
    List<Uni<String>> unis = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      // Propagate list with lazy asynchronous actions, which will be executed on subscription
      unis.add(Uni.createFrom().item(callUrl("https://api.chucknorris.io/jokes/random"))
        .runSubscriptionOn(executorService));
    }

    // Check the logs to see async http calls are being made on multiple threads
    unis.forEach(uni -> uni.subscribe().with(log::info));
  }

  private String callUrl(String url) {
    try {
      return httpClient.sendAsync(HttpRequest.newBuilder()
                  .GET().uri(URI.create(url)).build(),
              BodyHandlers.ofString())
          .thenApply(HttpResponse::body).toCompletableFuture().get();
    } catch (InterruptedException | ExecutionException ex) {
      throw new RuntimeException(ex);
    }
  }
}
