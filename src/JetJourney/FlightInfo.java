package JetJourney;

public class FlightInfo {

	private String timing, duration;
	private String stops, airline, website;
	private int price;

	public FlightInfo(String timing,
			String duration,
			String stops, String airline, String website, int price) {
		this.timing = timing;
		this.duration = duration;
		this.stops = stops;
		this.airline = airline;
		this.website = website;
		this.price = price;
	}

	public String getTiming() {
		return timing;
	}

	public String getFlightDuration() {
		return duration;
	}

	public String getNumStops() {
		return stops;
	}

	public String getAirline() {
		return airline;
	}

	public String getWebsite() {
		return website;
	}

	public int getPrice() {
		return price;
	}

}
