package com.FoodApp.FoodOrderingApp.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.metamodel.Attribute;
import org.springframework.beans.factory.annotation.Autowired;

@Converter
public class AddressConveerter implements AttributeConverter<Address, String> {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Address address) {
        try {
            return objectMapper.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        // Convert JSON string or other database value back to Address object
        try {
            return objectMapper.readValue(dbData, Address.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

