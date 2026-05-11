package co.edu.unbosque.projectFifaUbosque.service;

import co.edu.unbosque.projectFifaUbosque.dto.FlightDTO;
import co.edu.unbosque.projectFifaUbosque.model.User;
import co.edu.unbosque.projectFifaUbosque.repository.UserRepository;
import co.edu.unbosque.projectFifaUbosque.util.AESUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.*;

@Service
public class FlightService {

	@Value("${serpapi.api.key}")
	private String apiKey;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ExternalHTTPRequestHandler httpHandler;

	private static final Map<String, String> COUNTRY_AIRPORTS = Map.of("Colombia", "BOG", "Argentina", "EZE", "Brazil",
			"GRU", "France", "CDG", "Spain", "MAD", "Germany", "FRA");

	private static final String[] HOST_AIRPORTS = { "MEX", "GDL", "MTY", "JFK", "LAX", "MIA", "DFW", "ATL", "YYZ",
			"YVR", "YUL" };

	public Map<String, List<FlightDTO>> getFlightPackage(String username, String startDateStr, String endDateStr) {
		String originAirport = "BOG"; // Default
		Optional<User> userOpt = userRepo.findByUser(AESUtil.encrypt(username));
		if (userOpt.isPresent()) {
			String userCountry = userOpt.get().getCoutry();
			originAirport = COUNTRY_AIRPORTS.getOrDefault(userCountry, "BOG");
		}

		String destinationAirport = HOST_AIRPORTS[new Random().nextInt(HOST_AIRPORTS.length)];

		LocalDate startDate = LocalDate.parse(startDateStr);
		LocalDate endDate = LocalDate.parse(endDateStr);

		String outboundDate = startDate.minusDays(2).toString();
		String returnDate = endDate.plusDays(2).toString();

		Map<String, List<FlightDTO>> packageResult = new HashMap<>();

		packageResult.put("outbound", fetchOneWayFlight(originAirport, destinationAirport, outboundDate));
		packageResult.put("inbound", fetchOneWayFlight(destinationAirport, originAirport, returnDate));

		return packageResult;
	}

	private List<FlightDTO> fetchOneWayFlight(String dep, String arr, String date) {
		List<FlightDTO> flights = new ArrayList<>();

		String queryParams = UriComponentsBuilder.fromPath("search.json").queryParam("engine", "google_flights")
				.queryParam("departure_id", dep).queryParam("arrival_id", arr).queryParam("outbound_date", date)
				.queryParam("type", "2") 
				.queryParam("currency", "USD").queryParam("hl", "es").queryParam("api_key", apiKey).toUriString();

		try {
			String response = httpHandler.getFromSerpApi(queryParams);

			if (response == null || response.isBlank())
				return flights;

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response);
			JsonNode bestFlights = root.path("best_flights");

			if (bestFlights.isArray() && bestFlights.size() > 0) {
				JsonNode option = bestFlights.get(0);
				FlightDTO dto = new FlightDTO();
				dto.setPrice(option.path("price").asInt());

				JsonNode firstLeg = option.path("flights").get(0);
				if (firstLeg != null && !firstLeg.isMissingNode()) {
					dto.setAirline(firstLeg.path("airline").asText());
					dto.setAirlineLogo(firstLeg.path("airline_logo").asText());
					dto.setDepartureTime(firstLeg.path("departure_airport").path("time").asText());
					dto.setArrivalTime(firstLeg.path("arrival_airport").path("time").asText());
					flights.add(dto);
				}
			}
		} catch (Exception e) {
		}

		return flights;
	}
}