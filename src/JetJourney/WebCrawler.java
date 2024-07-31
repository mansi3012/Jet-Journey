package JetJourney;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.openqa.selenium.WebDriver;

public class WebCrawler {

	public void fetchAndSaveWebsites(String originCity, String destinationCity, int travelDay, int travelMonth,
			int travelYear,
			String cabinClass, int numTravelers, String outputFolderPath, WebDriver webDriver) {

		try {
			String originAirportCode = AirportCodeConverter.getAirportCode(originCity);
			String destinationAirportCode = AirportCodeConverter.getAirportCode(destinationCity);

			String formattedDay = formatDatePart(travelDay);
			String formattedMonth = formatDatePart(travelMonth);

			// Kayak
			String kayakURL = constructKayakURL(originAirportCode, destinationAirportCode, travelYear, formattedMonth,
					formattedDay, cabinClass, numTravelers);
			fetchAndSaveWebpage(kayakURL, originCity, destinationCity, travelDay, travelMonth, travelYear, "kayak",
					outputFolderPath, webDriver);

			// Booking
			String bookingURL = constructBookingURL(originAirportCode, destinationAirportCode, travelYear,
					formattedMonth, formattedDay, cabinClass, numTravelers);
			fetchAndSaveWebpage(bookingURL, originCity, destinationCity, travelDay, travelMonth, travelYear, "booking",
					outputFolderPath, webDriver);

			// Cheapflights
			String cheapflightsURL = constructCheapflightsURL(originAirportCode, destinationAirportCode, travelYear,
					formattedMonth, formattedDay, cabinClass, numTravelers);
			fetchAndSaveWebpage(cheapflightsURL, originCity, destinationCity, travelDay, travelMonth, travelYear,
					"cheapflights", outputFolderPath, webDriver);

			webDriver.manage().window().minimize();
		} catch (Exception e) {
			System.out.println("An error occurred during web crawling: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void fetchAndSaveWebpage(String url, String originCity, String destinationCity, int travelDay,
			int travelMonth, int travelYear,
			String website, String outputFolderPath, WebDriver webDriver) {
		try {
			webDriver.get(url);
			Thread.sleep(8000); // Wait for the page to load
			String htmlContent = webDriver.getPageSource();

			String fileName = constructFileName(originCity, destinationCity, travelDay, travelMonth, travelYear,
					website);

			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFolderPath + "/" + fileName));
			writer.write(htmlContent);
			writer.close();

			System.out.println("HTML File has been saved successfully for " + formatDatePart(travelDay) + "/"
					+ formatDatePart(travelMonth) + "/" + travelYear
					+ " from " + originCity + " to " + destinationCity + " on " + website.toUpperCase());
		} catch (IOException e) {
			System.out.println("There was an error while saving the HTML file: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("There was an error while waiting for the page to load: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String constructFileName(String originCity, String destinationCity, int travelDay, int travelMonth,
			int travelYear, String website) {
		return originCity.toLowerCase() + "_to_" + destinationCity.toLowerCase() + "_" + formatDatePart(travelDay) + "_"
				+ formatDatePart(travelMonth) + "_" + travelYear + "_" + website + ".html";
	}

	private String formatDatePart(int datePart) {
		return datePart < 10 ? "0" + datePart : "" + datePart;
	}

	private String constructKayakURL(String originAirportCode, String destinationAirportCode, int travelYear,
			String formattedMonth, String formattedDay, String cabinClass, int numTravelers) {
		return "https://www.ca.kayak.com/flights/" + originAirportCode + "-" + destinationAirportCode + "/" + travelYear
				+ "-" + formattedMonth + "-" + formattedDay + "/" + cabinClass + "/" + numTravelers
				+ "adults?sort=bestflight_a";
	}

	private String constructBookingURL(String originAirportCode, String destinationAirportCode, int travelYear,
			String formattedMonth, String formattedDay, String cabinClass, int numTravelers) {
		return "https://flights.booking.com/flights/" + originAirportCode + ".AIRPORT-" + destinationAirportCode
				+ ".AIRPORT/?type=ONEWAY&adults=" + numTravelers + "&cabinClass=" + cabinClass.toUpperCase()
				+ "&children=&from=" + originAirportCode + ".AIRPORT&to=" + destinationAirportCode + "&Airport&depart="
				+ travelYear + "-"
				+ formattedMonth + "-" + formattedDay + "&sort=BEST";
	}

	private String constructCheapflightsURL(String originAirportCode, String destinationAirportCode, int travelYear,
			String formattedMonth, String formattedDay, String cabinClass, int numTravelers) {
		return "https://www.cheapflights.ca/flight-search/" + originAirportCode + "-" + destinationAirportCode + "/"
				+ travelYear + "-" + formattedMonth + "-" + formattedDay + "/" + cabinClass + "/" + numTravelers
				+ "adults?sort=bestflight_a";
	}
}