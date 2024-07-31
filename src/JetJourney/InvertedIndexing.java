package JetJourney;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InvertedIndexing {

	private static class IndexNode {
		String term;
		int frequency;
		IndexNode left, right;
		List<TermOccurrence> occurrences;

		IndexNode(String term) {
			this.term = term;
			this.frequency = 1;
			this.occurrences = new ArrayList<>();
		}

		void addOccurrence(int pageNumber, int position, String fileName) {
			occurrences.add(new TermOccurrence(pageNumber, position, fileName));
		}
	}

	private static class TermOccurrence {
		int pageNumber;
		int position;
		String fileName;

		TermOccurrence(int pageNumber, int position, String fileName) {
			this.pageNumber = pageNumber;
			this.position = position;
			this.fileName = fileName;
		}
	}

	private IndexNode rootNode;

	public void createIndex(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			int pageNumber = 0;
			int position = 0;
			while ((line = reader.readLine()) != null) {
				String[] terms = line.split("\\s+");
				for (String term : terms) {
					String lowercaseTerm = term.toLowerCase();
					insertTerm(lowercaseTerm, pageNumber, position++, file.getName());
				}
				pageNumber++;
			}
		} catch (IOException e) {
			System.err.println("Error reading file: " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	private void insertTerm(String term, int pageNumber, int position, String fileName) {
		rootNode = insertRecursive(rootNode, term, pageNumber, position, fileName);
	}

	private IndexNode insertRecursive(IndexNode node, String term, int pageNumber, int position, String fileName) {
		if (node == null) {
			node = new IndexNode(term);
		}

		int compareResult = term.compareTo(node.term);
		if (compareResult < 0) {
			node.left = insertRecursive(node.left, term, pageNumber, position, fileName);
		} else if (compareResult > 0) {
			node.right = insertRecursive(node.right, term, pageNumber, position, fileName);
		} else {
			node.frequency++;
			node.addOccurrence(pageNumber, position, fileName);
		}

		return node;
	}

	public void searchTerm(String term) {
		IndexNode node = searchRecursive(rootNode, term.toLowerCase());
		if (node == null) {
			System.out.println("Term not found: " + term);
			return;
		}

		System.out.println("Occurrences of term: " + term);
		printOccurrences(node);
		System.out.println();
	}

	private IndexNode searchRecursive(IndexNode node, String term) {
		if (node == null) {
			return null;
		}

		int compareResult = term.compareTo(node.term);
		if (compareResult == 0) {
			return node;
		} else if (compareResult < 0) {
			return searchRecursive(node.left, term);
		} else {
			return searchRecursive(node.right, term);
		}
	}

	private void printOccurrences(IndexNode node) {
		for (TermOccurrence occurrence : node.occurrences) {
			System.out.println("Page Number: " + occurrence.pageNumber + ", Position: " + occurrence.position
					+ ", File Name: " + occurrence.fileName);
		}
	}

	public static void startIndexing(String term) {
		String folderPath = JetJourney.PARSED_FILES_PATH;
		InvertedIndexing index = new InvertedIndexing();

		File folder = new File(folderPath);
		if (!folder.exists()) {
			System.err.println("Folder not found: " + folderPath);
			return;
		}

		File[] files = folder.listFiles();
		if (files == null) {
			System.err.println("Error listing files in folder: " + folderPath);
			return;
		}

		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".txt")) {
				System.out.println("--------------------------------------");
				System.out.println("Indexing file: " + file.getName());
				index.createIndex(file);
			}
		}

		System.out.println("\n--------------------------------------");
		System.out.println("Searching for term: " + term);
		index.searchTerm(term);
	}
}