# Distributed Rate Limiter

A high-concurrency API rate limiting service built with Spring Boot and Redis. This project implements the Token Bucket algorithm using atomic Lua scripts to guarantee thread safety and prevent race conditions in distributed environments.

## Architecture & Features

* **Atomic Distributed State:** Evaluates and updates rate limit counters in a single atomic operation using Redis Lua scripting, ensuring absolute accuracy under heavy load.
* **Token Bucket Algorithm:** Enforces precise API throttling with configurable burst capacity and time-based token replenishment.
* **Concurrency Resiliency:** Engineered to handle massive synchronized traffic spikes. Tested against bursts of 1,000+ concurrent requests using `CountDownLatch` and fixed thread pools.
* **Infrastructure Tuned:** Customized embedded Tomcat configurations (accept-count, max-connections) to handle extreme socket backlogs and prevent connection starvation during siege events.

## Tech Stack

* **Framework:** Java, Spring Boot
* **Datastore:** Redis
* **Scripting:** Lua
* **Concurrency & Testing:** JUnit 5, Java `HttpClient`, `ExecutorService`

## Project Structure

* `src/main/java/.../config/`: Redis connection factories and template serialization configurations.
* `src/main/java/.../services/`: Core rate limiting business logic and script execution.
* `src/main/java/.../Controller/`: REST endpoints protected by the rate limiter.
* `src/main/resources/rate_limiter.lua`: The standalone Lua script managing token bucket math and expiration inside Redis.
