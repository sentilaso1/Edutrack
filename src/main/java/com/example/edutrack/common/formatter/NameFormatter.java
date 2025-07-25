package com.example.edutrack.common.formatter;

public class NameFormatter {
    public static String formatName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        String[] words = name.trim().toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return formatted.toString().trim();
    }

}
