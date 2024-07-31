package JetJourney;

import java.io.File;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BookingFileParser {

        public static ArrayList<FlightInfo> parseBookingFiles(String origin, String destination, int month,
                        int year, int day, String folderPath, int adults) {
                String monthString = "" + month;
                if (month < 10) {
                        monthString = "0" + month;
                }

                String dayString = null;

                if (day < 10) {
                        dayString = "0" + day;
                } else {
                        dayString = "" + day;
                }

                ArrayList<FlightInfo> bestFlights = new ArrayList<>();

                try {
                        String path = folderPath + "/" + origin.toLowerCase() + "_to_" + destination.toLowerCase() + "_"
                                        + dayString
                                        + "_" + monthString + "_" + year + "_Booking" + ".html";
                        File input = new File(path.toLowerCase());
                        Document doc = Jsoup.parse(input, "UTF-8");

                        Elements results = doc.getElementsByClass("Frame-module__padding_4___mVTBX");

                        for (var result : results) {

                                Elements duration = result.getElementsByClass("Stops-module__textStyle___3og-9");

                                if (duration.first() == null) {
                                        continue;
                                }

                                String newDuration = duration.first().text();
                                String stops = result.getElementsByClass("Stops-module__textStyle___3og-9").get(1)
                                                .text();

                                String airline = result
                                                .getElementsByAttributeValue("data-testid", "flight_card_carrier_0")
                                                .text();

                                String price;

                                if (adults > 1) {
                                        price = result
                                                        .getElementsByClass("FlightCardPrice-module__mainPrice___J-ldY")
                                                        .text()
                                                        .split(",")[0].replace(".", "");
                                } else {
                                        price = result
                                                        .getElementsByClass(
                                                                        "FlightCardPrice-module__priceContainer___nXXv2")
                                                        .text().split(",")[0].replace(".", "");
                                }

                                String departureTiming = result
                                                .getElementsByClass("Text-module__root--variant-strong_1___hIxEW")
                                                .text();

                                // System.out.println("Multiple: ");
                                // System.out.println("Timing: " + departureTiming);
                                // System.out.println("Price: " + price);
                                // System.out.println("Airline: " + airline);
                                // System.out.println("Duration: " + newDuration);
                                // System.out.println("Stops: " + stops);

                                FlightInfo currentFlight = new FlightInfo(departureTiming, newDuration,
                                                stops,
                                                airline, "Booking.com", Integer.parseInt(price));

                                bestFlights.add(currentFlight);
                        }
                } catch (Exception e) {
                        // Handle the exception
                        System.out.println("Error parsing Booking.com HTML data: " + e.getMessage());
                        System.out.println("\n-------------------------------------------------------");
                        System.out.println("There must be no flights on Booking.com for this Route or Date!");

                }

                return FlightSorter.sortFlightsByPrice(bestFlights);
        }
}
