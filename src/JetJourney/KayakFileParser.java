package JetJourney;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class KayakFileParser {

    public static ArrayList<FlightInfo> parseKayakFiles(String origin, String destination, int month, int year, int day,
            String folderPath) {
        String data = "";
        String monthStr = String.format("%02d", month); // Formatting month with leading zero
        String dayStr = String.format("%02d", day); // Formatting day with leading zero

        ArrayList<FlightInfo> topKayakFlights = new ArrayList<>();

        try {
            String filePath = folderPath + "/" + origin.toLowerCase() + "_to_" + destination.toLowerCase() + "_"
                    + dayStr
                    + "_" + monthStr + "_" + year + "_Kayak" + ".html";
            File file = new File(filePath.toLowerCase());
            Document doc = Jsoup.parse(file, "UTF-8");
            Elements results = doc.getElementsByClass("nrc6-wrapper");
            for (int i = 0; i < results.size(); i++) {
                Element result = results.get(i);

                String departureTime = result.getElementsByClass("vmXl-mod-variant-large").text();
                String originShort = result.getElementsByClass("c_cgF.c_cgF-mod-variant-full-airport-wide").text();

                String numStops = result.getElementsByClass("JWEO-stops-text").text();

                String airlineName = result.getElementsByClass("J0g6-operator-text").text();
                String flightDuration = result.getElementsByClass("vmXl-mod-variant-default").get(1).text();
                String price = result.getElementsByClass("f8F1-price-text").text().split(" ")[1].replace(",", "");

                FlightInfo currentFlight = new FlightInfo(departureTime, flightDuration, numStops,
                        airlineName, "Kayak", Integer.parseInt(price));

                topKayakFlights.add(currentFlight);
            }
        } catch (IOException e) {
            System.out.println("Error parsing Kayak data: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred while parsing Kayak data: " + e.getMessage());
            System.out.println("There might be no flights on Kayak.com for this route or date.");
        }

        return FlightSorter.sortFlightsByPrice(topKayakFlights);
    }

    public static void main(String[] args) {
        var flights = parseKayakFiles("toronto", "dubai", 12, 2024, 6, JetJourney.SRC_FOLDER_PATH + "tmp");

        for (var flight : flights) {
            System.out.println(flight.getPrice() + " " + flight.getAirline());
        }
    }
}