package JetJourney;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PageRankingEntry implements Comparable<PageRankingEntry> {
	private int count;
	private String fileName;

	public PageRankingEntry(int count, String fileName) {
		this.count = count;
		this.fileName = fileName;
	}

	public int getCount() {
		return count;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public int compareTo(PageRankingEntry other) {
		return Integer.compare(other.count, this.count);
	}
}

public class PageRanker {
	public static void rankPages(String folderPath, String keyword) throws FileNotFoundException {
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		List<PageRankingEntry> rankingEntries = new ArrayList<>();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					int count = countKeywordOccurrences(file, keyword);
					rankingEntries.add(new PageRankingEntry(count, file.getName()));
				}
			}
		}

		Collections.sort(rankingEntries);

		System.out.println("\n-----Page Ranking-----");
		for (PageRankingEntry entry : rankingEntries) {
			System.out.println(entry.getCount() + " " + entry.getFileName());
		}
	}

	private static int countKeywordOccurrences(File file, String keyword) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		int count = 0;
		String fileContent = scanner.useDelimiter("\\Z").next().toLowerCase();
		String pattern = "\\b" + keyword.toLowerCase() + "\\b";

		Matcher matcher = Pattern.compile(pattern).matcher(fileContent);
		while (matcher.find()) {
			count++;
		}

		scanner.close();
		return count;
	}
}