package org.example.library;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        jsonWriter.value(localDate != null ? localDate.format(FORMATTER) : null);
    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        String dateStr = jsonReader.nextString();
        if (dateStr.length() == 4) {
            dateStr = dateStr + "-01-01";
        } else if (!dateStr.contains("-")) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
        return LocalDate.parse(dateStr, FORMATTER);
    }
}
