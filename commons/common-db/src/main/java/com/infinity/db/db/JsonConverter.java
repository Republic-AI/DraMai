package com.infinity.db.db;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@Converter(autoApply = true)
public class JsonConverter implements AttributeConverter<Object, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error serializing object", e);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Object.class);  // Return as Object
        } catch (IOException e) {
            throw new IllegalArgumentException("Error deserializing JSON", e);
        }
    }
}
