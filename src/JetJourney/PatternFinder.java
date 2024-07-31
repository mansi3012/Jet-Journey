package JetJourney;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class PatternFinder {

    public static void findPatterns(String pattern, String folderPath) throws IOException {
        System.out.println("Searching for pattern: " + pattern);
        System.out.println("In folder: " + folderPath);

        Pattern regexPattern = Pattern.compile(pattern);
        java.io.File folder = new java.io.File(folderPath);

        if (!folder.isDirectory()) {
            System.err.println("Invalid folder path: " + folderPath);
            return;
        }

        java.io.File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("There is no files found in the specified folder.");
            return;
        }

        for (java.io.File file : files) {
            if (file.isFile()) {
                String fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                Matcher matcher = regexPattern.matcher(fileContent);
                System.out.println("\nFile: " + file.getName());
                while (matcher.find()) {
                    System.out.println(matcher.group());
                }
            }
        }
    }
}