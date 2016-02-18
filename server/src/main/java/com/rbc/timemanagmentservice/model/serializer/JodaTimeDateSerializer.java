package com.rbc.timemanagmentservice.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by russbaker on 2/13/16.
 */
@Component
public class JodaTimeDateSerializer extends JsonSerializer<DateTime> {
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2)throws IOException, JsonProcessingException {
        gen.writeString(formatter.print(value));
    }
}
