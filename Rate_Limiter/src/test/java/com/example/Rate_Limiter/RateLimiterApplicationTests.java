package com.example.Rate_Limiter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateLimiterApplicationTests {

    @LocalServerPort
    private int port;
	@Test
	public void executeSiege() throws InterruptedException {
        int totalRequests = 1000;
        CountDownLatch gate = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(totalRequests);

        AtomicInteger okCount = new AtomicInteger(0);
        AtomicInteger tooManyCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(200);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        for(int i=0; i<totalRequests; i++){
            executor.submit(()->{
               try{
                   gate.await();
                   HttpRequest request = HttpRequest.newBuilder()
                           .uri(URI.create("http://localhost:" + port + "/api/resource"))
                           .header("X-User-Id", "stress-target-001")
                           .GET()
                           .build();

                   HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                   if(response.statusCode() == 200)
                       okCount.incrementAndGet();
                   else if(response.statusCode() == 429)
                       tooManyCount.incrementAndGet();
                   else
                       failCount.incrementAndGet();
               }catch (Exception e) {
                   failCount.incrementAndGet();
               }finally {
                   completionLatch.countDown();
               }
            });
        }

        Thread.sleep(500);
        gate.countDown();
        completionLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("200 OK: " + okCount.get());
        System.out.println("429 Too Many Requests: " + tooManyCount.get());
        System.out.println("Failures: " + failCount.get());

        Assertions.assertEquals(100, okCount.get());
        Assertions.assertEquals(900, tooManyCount.get());
        Assertions.assertEquals(0, failCount.get());
	}

}
