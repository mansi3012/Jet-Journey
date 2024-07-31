package JetJourney;

import java.io.*;
import java.util.*;

/**
 * This class manages the search frequency tracking for specific words.
 */
public class SearchFrequency {
    private static final String FREQUENCY_DATA_FILE = JetJourney.SRC_FOLDER_PATH + "searchFrequency.csv";

    /**
     * Tracks the search frequency for a given word and returns the corrected word.
     *
     * @param inputWord The word to track search frequency for.
     * @return The corrected word after spell-checking and user input.
     * @throws Exception If an error occurs during the frequency tracking process.
     */
    public String trackSearchFrequency(String inputWord) throws Exception {
        WordCompletion wordCompletion = new WordCompletion();

        String searchWord = inputWord;
        boolean isWordValid = SpellChecker.checkandSuggestWords(searchWord);
        Scanner scanner = new Scanner(System.in);

        while (!isWordValid) {
            System.out.println("Enter the word again: ");
            searchWord = scanner.nextLine().toLowerCase();
            wordCompletion.processWordCompletions(searchWord);
            isWordValid = SpellChecker.checkandSuggestWords(searchWord);
        }

        Map<String, Integer> wordFrequencies = loadWordFrequencies();

        searchWord = searchWord.toLowerCase();
        int currentFrequency = wordFrequencies.getOrDefault(searchWord, 0);
        System.out.print("\n-----Frequency Value-----");
        System.out.print("\n" + searchWord + " was searched for " + currentFrequency + " times.");
        wordFrequencies.put(searchWord, currentFrequency + 1);
        saveWordFrequencies(wordFrequencies);
        return searchWord;
    }

    /**
     * Loads the word frequency data from the CSV file.
     *
     * @return A TreeMap containing word frequencies.
     * @throws IOException If an error occurs during file reading.
     */
    private static Map<String, Integer> loadWordFrequencies() throws IOException {
        Map<String, Integer> wordFrequencies = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FREQUENCY_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String word = parts[0];
                int frequency = Integer.parseInt(parts[1]);
                wordFrequencies.put(word, frequency);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while loading word frequency data.");
        }
        return wordFrequencies;
    }

    /**
     * Saves the word frequency data to the CSV file.
     *
     * @param wordFrequencies A TreeMap containing word frequencies.
     * @throws IOException If an error occurs during file writing.
     */
    private static void saveWordFrequencies(Map<String, Integer> wordFrequencies) throws IOException {
        try (FileWriter writer = new FileWriter(FREQUENCY_DATA_FILE)) {
            for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
                String word = entry.getKey();
                int frequency = entry.getValue();
                writer.append(word).append(",").append(Integer.toString(frequency)).append("\n");
            }
        }
    }
}