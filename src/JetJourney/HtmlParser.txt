package JetJourney;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class HtmlParser {

	/**
	 * Processes all HTML files in the given folder path and writes their text
	 * content to separate text files.
	 *
	 * @param folderPath The path of the folder containing HTML files.
	 */
	public static void parseHtmlFiles(String folderPath) {
		Path folder = Paths.get(folderPath);

		try (Stream<Path> paths = Files.walk(folder)) {
			// Filter out regular files with .html extension

			paths.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".html"))
					.forEach(HtmlParser::processHtmlFile);
		} catch (IOException e) {
			System.err.println("Error accessing folder: " + e.getMessage());
		}
	}

	/**
	 * Processes a single HTML file by parsing its content and writing it to a text
	 * file.
	 *
	 * @param filePath The path of the HTML file to process.
	 */
	private static void processHtmlFile(Path filePath) {
		try {
			File htmlFile = filePath.toFile();
			Document doc = Jsoup.parse(htmlFile, "UTF-8", "http://example.com/");
			String outputFileName = JetJourney.PARSED_FILES_PATH + htmlFile.getName() + ".txt";
			writeToFile(outputFileName, doc);
		} catch (IOException e) {
			System.err.println("Error processing file: " + filePath + ": " + e.getMessage());
		}
	}

	/**
	 * Writes the text content of a JSoup Document to a file.
	 *
	 * @param fileName The name of the output file.
	 * @param doc      The JSoup Document containing the HTML content.
	 * @throws IOException If an I/O error occurs while writing the file.
	 */
	private static void writeToFile(String fileName, Document doc) throws IOException {
		try (FileWriter writer = new FileWriter(fileName)) {
			int index = 0;
			while (!doc.getElementsByIndexEquals(index).isEmpty()) {
				String text = doc.getElementsByIndexEquals(index).text();
				if (!text.isEmpty()) {
					writer.write(text);
					writer.write(System.lineSeparator());
				}
				index++;
			}
		} catch (IOException e) {
			System.err.println("Error writing to file: " + fileName + ": " + e.getMessage());
		}
	}
}