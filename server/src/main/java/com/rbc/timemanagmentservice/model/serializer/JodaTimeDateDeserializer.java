package com.rbc.timemanagmentservice.model.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Created by russbaker on 3/3/16.
 */
public class JodaTimeDateDeserializer extends JsonDeserializer<DateTime>{
    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");
        JsonToken currentToken = jp.getCurrentToken();
        if(currentToken.equals(JsonToken.VALUE_STRING)){
            return FMT.parseDateTime(jp.getText().trim()).withTimeAtStartOfDay();
        }



        return null;
    }
}
