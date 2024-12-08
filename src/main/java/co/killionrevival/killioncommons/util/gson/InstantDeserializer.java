package co.killionrevival.killioncommons.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantDeserializer extends TypeAdapter<Instant> {
    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.toString());
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            in.nextNull();
            return null;
        }

        return Instant.parse(in.nextString());
    }
}
