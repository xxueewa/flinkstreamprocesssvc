package com.example.quizcard.flinkapp.source;

import com.example.quizcard.flinkapp.model.Attempt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class EventRecordDeserializer implements DeserializationSchema<Attempt> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Attempt deserialize(byte[] bytes) throws IOException {
        return mapper.readValue(bytes, Attempt.class);
    }

    @Override
    public boolean isEndOfStream(Attempt attempt) {
        return false;
    }

    @Override
    public TypeInformation<Attempt> getProducedType() {
        return TypeInformation.of(Attempt.class);
    }
}
