package org.rakam.collection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.inject.Inject;
import java.io.IOException;

public class EventListDeserializer extends JsonDeserializer<EventList> {

    private final EventListDeserializationService service;

    @Inject
    public EventListDeserializer(EventListDeserializationService service) {
        this.service = service;
    }

    @Override
    public EventList deserialize(
            JsonParser jp,
            DeserializationContext deserializationContext) throws IOException {

        return service.deserialize(jp, deserializationContext);
    }
}
