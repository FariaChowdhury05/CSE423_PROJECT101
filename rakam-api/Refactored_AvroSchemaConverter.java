package org.rakam.analysis;

import com.google.common.collect.Lists;
import org.apache.avro.Schema;
import org.rakam.collection.FieldType;
import org.rakam.collection.SchemaField;
import org.rakam.util.AvroUtil;
import org.rakam.util.RakamException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.apache.avro.Schema.Type.NULL;

public final class AvroSchemaConverter {

    private AvroSchemaConverter() {
    }

    public static Set<SchemaField> convert(String schemaFields) {

        Schema parse =
                new Schema.Parser().parse(schemaFields);

        if (parse.getType() != Schema.Type.RECORD) {
            throw new RakamException(
                    "Avro schema must be a RECORD",
                    BAD_REQUEST
            );
        }

        Set<SchemaField> rakamFields =
                new HashSet<>();

        for (Schema.Field field : parse.getFields()) {

            Schema avroSchema =
                    resolveSchema(field.schema());

            Optional<FieldType> fieldType =
                    findFieldType(avroSchema);

            if (!fieldType.isPresent()) {
                throw new RakamException(
                        "Unsupported Avro type" + avroSchema,
                        BAD_REQUEST
                );
            }

            rakamFields.add(
                    new SchemaField(
                            field.name(),
                            fieldType.get(),
                            avroSchema.getFullName(),
                            avroSchema.getDoc(),
                            null
                    )
            );
        }

        return rakamFields;
    }

    private static Schema resolveSchema(Schema schema) {

        if (schema.getType() != Schema.Type.UNION) {
            return schema;
        }

        List<Schema> types = schema.getTypes();

        if (types.isEmpty()) {
            throw new IllegalStateException();
        }

        if (types.size() == 1) {
            return types.get(0);
        }

        if (types.size() == 2) {

            if (types.get(0).getType() == NULL) {
                return Schema.createUnion(
                        Lists.newArrayList(
                                Schema.create(NULL),
                                types.get(1)
                        )
                );
            }

            if (types.get(1).getType() == NULL) {
                return Schema.createUnion(
                        Lists.newArrayList(
                                Schema.create(NULL),
                                types.get(0)
                        )
                );
            }
        }

        throw new RakamException(
                "UNION type is not supported: " + schema,
                BAD_REQUEST
        );
    }

    private static Optional<FieldType> findFieldType(
            Schema schema) {

        return Arrays.stream(FieldType.values())
                .filter(type ->
                        AvroUtil.generateAvroSchema(type)
                                .equals(schema))
                .findAny();
    }
}
