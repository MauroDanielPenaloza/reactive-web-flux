package com.example.reactiveclient;

import org.apache.commons.logging.impl.NoOpLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalTime;

@SpringBootApplication
public class ReactiveClientApplication {

	private  static Logger logger = LoggerFactory.getLogger(ReactiveClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactiveClientApplication.class, args);
		consumeServerSentEvent();
	}
	public static void consumeServerSentEvent() {
		WebClient client = WebClient.create("http://localhost:8080");
		ParameterizedTypeReference<ServerSentEvent<PersonPOJO>> type
				= new ParameterizedTypeReference<ServerSentEvent<PersonPOJO>>() {
		};

		Flux<ServerSentEvent<PersonPOJO>> eventStream = client.get()
				.uri("/stream-sse")
				.retrieve()
				.bodyToFlux(type);

		eventStream.subscribe(
				content -> logger.info("Time: {} - event: name[{}], id [{}], content[{}] ",
						LocalTime.now(), content.event(), content.id(), content.data()),
				error -> logger.error("Error receiving SSE: {}", error),
				() -> logger.info("Completed!!!"));
	}
}
