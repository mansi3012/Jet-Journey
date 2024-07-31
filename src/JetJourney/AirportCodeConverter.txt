package JetJourney;

import java.io.*;
import java.util.*;

public class AirportCodeConverter {
  public static String getAirportCode(String fullCityName) {
    Map<String, String> airportCodes = new HashMap<>();
    String cityLine;
    String airportCodeForCity;

    try {
      BufferedReader cityReader = new BufferedReader(new FileReader(JetJourney.SRC_FOLDER_PATH + "cities.txt"));
      BufferedReader codeReader = new BufferedReader(new FileReader(JetJourney.SRC_FOLDER_PATH + "codes.txt"));

      while ((cityLine = cityReader.readLine()) != null) {
        airportCodeForCity = codeReader.readLine();
        airportCodes.put(cityLine.toLowerCase().trim(), airportCodeForCity);
      }

      cityReader.close();
      codeReader.close();

      return airportCodes.get(fullCityName.toLowerCase().trim());
    } catch (Exception e) {
      System.out.println("Error while reading city names: " + e.getMessage());
      return null;
    }
  }
}