package org.rakam.analysis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.rakam.collection.SchemaField;

import java.util.Set;
import java.util.function.Function;

public enum SchemaConverter {

    AVRO(AvroSchemaConverter::convert);

    private final Function<String, Set<SchemaField>> mapper;

    SchemaConverter(Function<String, Set<SchemaField>> mapper) {
        this.mapper = mapper;
    }

    @JsonCreator
    public static SchemaConverter get(String name) {
        return valueOf(name.toUpperCase());
    }

    @JsonProperty
    public String value() {
        return name();
    }

    public Function<String, Set<SchemaField>> getMapper() {
        return mapper;
    }
}
