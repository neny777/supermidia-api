package br.com.supermidia.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LowercaseConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute != null ? attribute.toLowerCase().trim() : null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}