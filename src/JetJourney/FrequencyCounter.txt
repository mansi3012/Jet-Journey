package JetJourney;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

class FrequencyData {
	private int count;
	private String fileName;

	FrequencyData(int count, String fileName) {
		this.count = count;
		this.fileName = fileName;
	}

	int getCount() {
		return count;
	}

	String getFileName() {
		return fileName;
	}
}

public class FrequencyCounter {

	static List<FrequencyData> frequencyList = new ArrayList<>();

	public static void countFrequency(String folderPath, String pattern) {
		try {
			File folder = new File(folderPath);
			File[] files = folder.listFiles();

			if (files == null) {
				System.out.println("Error: Invalid folder path or folder is empty.");
				return;
			}

			for (File file : files) {
				if (file.isFile()) {
					processFile(file, folderPath, pattern);
				}
			}

			printFrequencies();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void processFile(File file, String folderPath, String pattern) throws FileNotFoundException {
		String text = readFileContent(file, folderPath);
		int frequency = findPatternFrequency(text, pattern);
		frequencyList.add(new FrequencyData(frequency, file.getName()));
	}

	private static String readFileContent(File file, String folderPath) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(folderPath + "/" + file.getName()));
		scanner.useDelimiter("\\Z");
		String fileContent = scanner.nextLine().toLowerCase();
		scanner.close();
		return sanitizeTextContent(fileContent);
	}

	private static String sanitizeTextContent(String rawText) {
		StringBuilder cleanedTextBuilder = new StringBuilder();
		StringTokenizer wordTokenizer = new StringTokenizer(rawText);

		while (wordTokenizer.hasMoreTokens()) {
			String currentWord = wordTokenizer.nextToken();
			if (Pattern.matches("[a-zA-Z0-9]+", currentWord)) {
				cleanedTextBuilder.append(currentWord).append(" ");
			}
		}

		return cleanedTextBuilder.toString();
	}

	private static int findPatternFrequency(String text, String pattern) {
		int count = 0;
		int textLength = text.length();
		int patternLength = pattern.length();
		int index = 0;

		BoyerMoore boyerMoore = new BoyerMoore(pattern);
		int findOffset = boyerMoore.search(text, index);

		while (index < (textLength - patternLength)) {
			boyerMoore = new BoyerMoore(pattern);
			findOffset = boyerMoore.search(text, index);
			index = 1 + findOffset;
			if (findOffset != textLength) {
				count++;
			}
		}

		return count;
	}

	private static void printFrequencies() {
		System.out.println("\nPattern Frequencies:");
		for (FrequencyData data : frequencyList) {
			System.out.println("File: " + data.getFileName() + ", Frequency: " + data.getCount());
		}
		System.out.println();
	}
}