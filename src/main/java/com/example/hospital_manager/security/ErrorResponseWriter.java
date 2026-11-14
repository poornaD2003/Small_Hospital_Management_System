package com.example.hospital_manager.security;


import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ErrorResponseWriter {

    public void write(
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String path) throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String body = "{\"timestamp\":" + quote(LocalDateTime.now().toString())
                + ",\"status\":" + status
                + ",\"error\":" + quote(error)
                + ",\"message\":" + quote(message)
                + ",\"path\":" + quote(path) + "}";
        response.getWriter().write(body);
    }

    private String quote(String value) {
        if (value == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder("\"");
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> json.append("\\\"");
                case '\\' -> json.append("\\\\");
                case '\b' -> json.append("\\b");
                case '\f' -> json.append("\\f");
                case '\n' -> json.append("\\n");
                case '\r' -> json.append("\\r");
                case '\t' -> json.append("\\t");
                default -> {
                    if (character < 0x20) {
                        json.append(String.format("\\u%04x", (int) character));
                    } else {
                        json.append(character);
                    }
                }
            }
        }
        return json.append('"').toString();
    }
}