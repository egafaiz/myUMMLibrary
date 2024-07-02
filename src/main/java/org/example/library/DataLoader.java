package org.example.library;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DataLoader {

    private static final String MAHASISWA_JSON_PATH = "src/main/resources/org/example/library/mahasiswa.json";

    public List<Map<String, String>> loadMahasiswa() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(MAHASISWA_JSON_PATH)) {
            Type listType = new TypeToken<List<Map<String, String>>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getMahasiswaByNIM(String nim) {
        List<Map<String, String>> mahasiswaList = loadMahasiswa();
        if (mahasiswaList != null) {
            for (Map<String, String> mahasiswa : mahasiswaList) {
                if (mahasiswa.get("nim").equals(nim)) {
                    return mahasiswa;
                }
            }
        }
        return null;
    }
}
