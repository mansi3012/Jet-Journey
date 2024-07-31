package JetJourney;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FlightSorter {

	public static ArrayList<FlightInfo> sortFlightsByPrice(ArrayList<FlightInfo> flights) {
		Collections.sort(flights, new Comparator<FlightInfo>() {
			@Override
			public int compare(FlightInfo flight1, FlightInfo flight2) {
				return Integer.compare(flight1.getPrice(), flight2.getPrice());
			}
		});
		return flights;
	}
}
