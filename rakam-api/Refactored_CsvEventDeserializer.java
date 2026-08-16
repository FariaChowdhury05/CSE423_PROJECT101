package org.rakam.collection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.inject.Inject;
import java.io.IOException;

public class CsvEventDeserializer extends JsonDeserializer<EventList> {

    private final CsvEventDeserializerService service;

    @Inject
    public CsvEventDeserializer(CsvEventDeserializerService service) {
        this.service = service;
    }

    @Override
    public EventList deserialize(
            JsonParser jp,
            DeserializationContext ctxt) throws IOException {

        return service.deserialize(jp, ctxt);
    }
}
