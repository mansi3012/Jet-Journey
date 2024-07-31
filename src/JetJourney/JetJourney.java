package JetJourney;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import java.util.regex.Pattern;

public class JetJourney {
	// Paths for source folder and parsed files folder
	public static final String SRC_FOLDER_PATH = "/Users/brijetasolanki/Documents/GitHub/JetJourney/FlightPriceAnalysis/src/";
	public static final String PARSED_FILES_PATH = "/Users/brijetasolanki/Documents/GitHub/JetJourney/FlightPriceAnalysis/src/JetJourney/parsedFiles/";
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";

	private static final String DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d\\d$";
	private static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEX);

	private static void displayflightInfo(ArrayList<FlightInfo> flightInfo, int numTravelers, String origin,
			String destination) {
		for (int i = 0; i < 3; i++) {
			if (flightInfo.get(i) != null) {

				String originAirportCode = AirportCodeConverter.getAirportCode(origin);
				String destinationAirportCode = AirportCodeConverter.getAirportCode(destination);

				FlightInfo flight = flightInfo.get(i);

				System.out.println(originAirportCode + " - " + destinationAirportCode
						+ " " + flight.getTiming());
				System.out.println(flight.getFlightDuration() + " " + flight.getNumStops() + " "
						+ flight.getAirline());

				if (numTravelers > 1) {
					System.out.println(ANSI_GREEN + "*CAD $ " + flight.getPrice() + " Per Person" + ANSI_RESET);
					System.out.println(ANSI_GREEN + "*CAD $ " + flight.getPrice() * numTravelers + " Total Cost"
							+ ANSI_RESET);
				} else {
					System.out.println(ANSI_GREEN + "*CAD $ " + flight.getPrice() + "* From: "
							+ flight.getWebsite() + ANSI_RESET);
				}

				System.out.println("-------------------------");
			} else {
				System.out.println("Error: FlightInfo object is possibly null at index " + i);
			}
		}
	}

	private static boolean isValidDate(String dateString) {
		return DATE_PATTERN.matcher(dateString).matches();
	}

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		WordCompletion wordCompletion = new WordCompletion();
		SearchFrequency searchFrequency = new SearchFrequency();

		LocalDate today = LocalDate.now();
		System.out.println(ANSI_YELLOW + "Today's date: " + today + ANSI_RESET);

		while (true) {
			System.out.println("\n============ WELCOME TO Flight Booking App ============");
			System.out.println("\n ==== Find the best flight deals for your travels. ===");
			System.out.println("\nSelect an option:");
			System.out.println("[1] Search for flights");
			System.out.println("[2] Exit");
			System.out.print("Input : ");
			String option = scanner.nextLine();

			if (option.equals("1")) {
				String origin = getValidInput("Enter the Origin place: ", scanner, true);
				wordCompletion.processWordCompletions(origin);

				origin = searchFrequency.trackSearchFrequency(origin);

				String destination = getValidInput("Enter the Destination place: ", scanner, true);

				while (origin.equalsIgnoreCase(destination)) {
					System.out.println(ANSI_RED
							+ "You have entered the same origin and desitination. Please enter a different destination"
							+ ANSI_RESET);
					destination = getValidInput("Enter the Destination place: ", scanner, true);
				}

				wordCompletion.processWordCompletions(destination);

				destination = searchFrequency.trackSearchFrequency(destination);

				int numTravelers = getValidNumericInput("Enter the number of travelers: ", scanner);
				String flightClass = getValidFlightClass(scanner);
				String departureDate = getValidDate(scanner);

				WebDriver driver = new EdgeDriver();
				String folderPath = SRC_FOLDER_PATH + "htmlFiles";

				WebCrawler webCrawler = new WebCrawler();
				webCrawler.fetchAndSaveWebsites(origin, destination, getDayFromDate(departureDate),
						getMonthFromDate(departureDate), getYearFromDate(departureDate), flightClass, numTravelers,
						folderPath, driver);

				ArrayList<FlightInfo> kayakFlights = KayakFileParser.parseKayakFiles(origin, destination,
						getMonthFromDate(departureDate), getYearFromDate(departureDate), getDayFromDate(departureDate),
						folderPath);
				ArrayList<FlightInfo> bookingFlights = BookingFileParser.parseBookingFiles(origin, destination,
						getMonthFromDate(departureDate), getYearFromDate(departureDate), getDayFromDate(departureDate),
						folderPath, numTravelers);
				ArrayList<FlightInfo> cheapFlightsFlights = CheapFlightsFileParser.parseCheapFlightsFiles(origin,
						destination,
						getMonthFromDate(departureDate), getYearFromDate(departureDate), getDayFromDate(departureDate),
						folderPath);

				if (kayakFlights != null && bookingFlights != null && cheapFlightsFlights != null) {
					System.out.println("\nTop 3 best flights from Kayak:");
					displayflightInfo(kayakFlights, numTravelers, origin, destination);

					System.out.println("\nTop 3 best flights from CheapFlights:");
					displayflightInfo(cheapFlightsFlights, numTravelers, origin, destination);

					System.out.println("\nTop 3 best flights from Booking.com:");
					displayflightInfo(bookingFlights, numTravelers, origin, destination);
				} else {
					System.out.println("Error: There was no data found in one or more parsers");
				}

				processFiles(folderPath, PARSED_FILES_PATH);
				initiateInvertedIndexing(scanner);
				initiatePatternSearch(scanner);
				initiateFrequencyCount(scanner);
				initiatePageRanking(scanner, folderPath);
				deleteFiles(folderPath);
				deleteFiles(PARSED_FILES_PATH);
			} else if (option.equals("2")) {
				System.out.println("Exiting Flight Booking App. Thank you for using our service!");
				break;
			} else {
				System.out.println(ANSI_RED + "Invalid input! Please enter 1 or 2" + ANSI_RESET);
			}

			if (!promptToContinue(scanner)) {
				break;
			}
		}

		scanner.close();
	}

	private static void initiateInvertedIndexing(Scanner userInput) {
		// Inverted Indexing
		System.out.println();
		System.out.println("\nEnter any keyword to start inverted indexing: ");
		String searchTerm = userInput.nextLine();

		try {
			InvertedIndexing.startIndexing(searchTerm);
		} catch (Exception e) {
			System.out.println("An error occurred during inverted indexing: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void initiatePatternSearch(Scanner userInput) {
		System.out.println();
		System.out.println("\nEnter the type of pattern you want to search for:");
		System.out.println("[1] Email");
		System.out.println("[2] Phone Number");
		System.out.println("[3] Price");
		System.out.print("Input: ");
		int patternChoice = userInput.nextInt();
		userInput.nextLine(); // Consume the newline character

		String pattern = "";
		String patternDescription = "";

		switch (patternChoice) {
			case 1:
				pattern = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b";
				patternDescription = "email patterns";
				break;
			case 2:
				pattern = "\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b";
				patternDescription = "phone number patterns";
				break;
			case 3:
				pattern = "C\\$[\\s]+[0-9]*";
				patternDescription = "price patterns";
				break;
			default:
				System.out.println("\nInvalid choice. Please try again.");
				break;
		}

		if (!pattern.isEmpty()) {
			System.out.println("\nSearching for " + patternDescription + "...");
			try {
				PatternFinder.findPatterns(pattern, PARSED_FILES_PATH);
			} catch (IOException e) {
				System.out.println("An error occurred during pattern search: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("\nNo pattern search performed.");
		}
	}

	private static void initiateFrequencyCount(Scanner userInput) {
		// Frequency Count
		System.out.println();
		System.out.println("\nEnter any keyword to start frequency counting: ");
		String frequencyTerm = userInput.nextLine();

		try {
			FrequencyCounter.countFrequency(PARSED_FILES_PATH, frequencyTerm);
		} catch (Exception e) {
			System.out.println("An error occurred during frequency counting: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void initiatePageRanking(Scanner userInput, String folderPath) {
		// Page Ranking
		System.out.println();
		System.out.println("\nEnter any keyword to start page ranking: ");
		String rankingTerm = userInput.nextLine();

		try {
			PageRanker.rankPages(folderPath, rankingTerm);
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred during page ranking: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static String getValidInput(String prompt, Scanner scanner, boolean allowEmpty) {
		String input;
		boolean isValid;

		do {
			System.out.print("\n" + prompt);
			input = scanner.nextLine().trim().toLowerCase();

			isValid = true;

			if (input.isEmpty()) {
				if (allowEmpty) {
					isValid = true;
				} else {
					System.out.println(ANSI_RED + "Input cannot be empty. Please try again." + ANSI_RESET);
					isValid = false;
				}
			} else if (input.matches(".*\\d+.*")) {
				System.out.println(ANSI_RED + "Input cannot contain numbers. Please try again." + ANSI_RESET);
				isValid = false;
			}
		} while (!isValid);

		return input;
	}

	private static int getValidNumericInput(String prompt, Scanner scanner) {
		int input;
		boolean isValid;

		do {
			System.out.print("\n" + prompt);
			String inputString = scanner.nextLine().trim();

			try {
				input = Integer.parseInt(inputString);
				isValid = input > 0;

				if (!isValid) {
					System.out.println(ANSI_RED + "Input must be greater than 0. Please try again." + ANSI_RESET);
				}
			} catch (NumberFormatException e) {
				System.out.println(ANSI_RED + "Invalid input. Please enter a valid number." + ANSI_RESET);
				isValid = false;
				input = 0;
			}
		} while (!isValid);

		return input;
	}

	private static String getValidFlightClass(Scanner scanner) {
		String flightClass;
		boolean isValid;

		do {
			System.out.print("\nEnter the flight class (Economy, Business, or First): ");
			flightClass = scanner.nextLine().trim().toLowerCase();

			isValid = flightClass.equals("economy") || flightClass.equals("business") || flightClass.equals("first");

			if (!isValid) {
				System.out.println(
						ANSI_RED + "Invalid flight class. Please enter Economy, Business, or First." + ANSI_RESET);
			}
		} while (!isValid);

		return flightClass;
	}

	private static String getValidDate(Scanner scanner) {
		String dateString;
		boolean isValid;

		do {
			System.out.print("\nDate [dd/mm/yyyy] : ");
			dateString = scanner.nextLine().trim();

			isValid = isValidDate(dateString);

			if (isValid) {
				String[] dateParts = dateString.split("/");
				LocalDate enteredDate = LocalDate.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]),
						Integer.parseInt(dateParts[0]));
				isValid = enteredDate.isAfter(LocalDate.now());
			}

			if (!isValid) {
				System.out.println("\nPlease enter a future date and in the format of dd/mm/yyyy.\n");
			}
		} while (!isValid);

		return dateString;
	}

	private static int getDayFromDate(String dateString) {
		return Integer.parseInt(dateString.split("/")[0]);
	}

	private static int getMonthFromDate(String dateString) {
		return Integer.parseInt(dateString.split("/")[1]);
	}

	private static int getYearFromDate(String dateString) {
		return Integer.parseInt(dateString.split("/")[2]);
	}

	private static void processFiles(String folderPath, String parsedFilesFolderPath) {
		String folderpaths = SRC_FOLDER_PATH + "tmp";
		File folder = new File(folderpaths);
		// Print the names of the HTML files in the folder
		for (File f : folder.listFiles()) {
			if (f.isFile() && f.getName().endsWith(".html")) {
				System.out.println("Converted the following file to txt: " + f.getName());
			}
		}
		HtmlParser.parseHtmlFiles(folderpaths);

	}

	private static void deleteFiles(String folderPath) {
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					file.delete();
				}
			}
		}
	}

	private static boolean promptToContinue(Scanner scanner) {
		String input;
		boolean isValid;

		do {
			System.out.println("\n================================================================================");
			System.out.println("Do you want to continue? (yes/no)");
			input = scanner.nextLine().trim().toLowerCase();

			isValid = input.equals("yes") || input.equals("no");

			if (!isValid) {
				System.out.println(ANSI_RED + "Invalid input! Please enter 'yes' or 'no'" + ANSI_RESET);
			}
		} while (!isValid);

		return input.equals("yes");
	}
}
