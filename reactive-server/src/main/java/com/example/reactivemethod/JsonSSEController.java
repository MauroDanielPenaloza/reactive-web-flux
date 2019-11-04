package com.example.reactivemethod;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class JsonSSEController {

    private static List<FluxSink<PersonPOJO>> myEmitter = new ArrayList<>();
    private static final Flux<ServerSentEvent<PersonPOJO>> fluxPerson;

    static  {
        fluxPerson = Flux.<PersonPOJO>create(emitter -> myEmitter.add(emitter))
                .map(personPOJO ->
                        ServerSentEvent.<PersonPOJO>builder()
                                .id(personPOJO.getNombre())
                                .event("person-event")
                                .data(personPOJO)
                                .build());
    }

    @GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Flux - " + LocalTime.now().toString());
    }

    @PostMapping("/add-person")
    public PersonPOJO addPerson(@RequestBody PersonPOJO personPOJO) {
        for (FluxSink<PersonPOJO> emitter:
             myEmitter) {
            emitter.next(personPOJO);
        }
        return personPOJO;
    }


    @GetMapping("/stream-sse")
    public Flux<ServerSentEvent<PersonPOJO>> streamEvents() {

        return fluxPerson;


    }
}
