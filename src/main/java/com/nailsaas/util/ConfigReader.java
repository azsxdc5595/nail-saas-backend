package com.nailsaas.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ConfigReader {

    public static Map<String, String> readConfig(String path) {

        Map<String, String> map = new HashMap<>();

        try {
            BufferedReader reader;
            if (path.startsWith("classpath:")) {

                String realPath = path.replace("classpath:", "");

                InputStream is = ConfigReader.class
                        .getClassLoader()
                        .getResourceAsStream(realPath);

                if (is == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到檔案：" + realPath);
                }

                reader = new BufferedReader(new InputStreamReader(is));

            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            }

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("=", 2);

                if (parts.length == 2) {
                    map.put(parts[0].trim(), parts[1].trim());
                }
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "讀取Mail設定檔失敗", e);
        }

        return map;
    }
}