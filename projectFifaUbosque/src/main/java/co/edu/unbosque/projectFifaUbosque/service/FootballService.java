package co.edu.unbosque.projectFifaUbosque.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class FootballService {

	@Autowired
	private ExternalHTTPRequestHandler httpHandler;

	@Value("${api.football.key}")
	private String apiKey;

	private static final String BASE_URL = "https://v3.football.api-sports.io/";
	private static final String AUTH_HEADER = "x-apisports-key";

	private static final Map<String, Integer> TEAM_IDS = Map.ofEntries(Map.entry("argentina", 26),
			Map.entry("brasil", 6), Map.entry("colombia", 8), Map.entry("francia", 67), Map.entry("españa", 9),
			Map.entry("inglaterra", 10), Map.entry("estados unidos", 227), Map.entry("méxico", 16),
			Map.entry("mexico", 16), Map.entry("alemania", 25), Map.entry("portugal", 27));

	private String cachedQualifiedTeams = null;
	private LocalDateTime teamsLastUpdate = null;
	private Map<String, String> cachedFixtures = new HashMap<>();
	private Map<String, LocalDateTime> fixturesLastUpdate = new HashMap<>();

	private String cachedWorldCupData = null;
	private LocalDateTime lastUpdate = null;

	public String getWorldCupTeams() {

		if (cachedWorldCupData != null && lastUpdate != null
				&& ChronoUnit.HOURS.between(lastUpdate, LocalDateTime.now()) < 24) {
			System.out.println("Devolviendo equipos desde el Caché local...");
			return cachedWorldCupData;
		}

		System.out.println("Consultando API-Football consumiendo 1 Request...");
		String url = BASE_URL + "teams?league=1&season=2022";
		String response = httpHandler.doGetWithAuth(url, AUTH_HEADER, apiKey);

		if (response != null && !response.contains("\"errors\":{")) {
			cachedWorldCupData = response;
			lastUpdate = LocalDateTime.now();
		}

		return response;
	}

	public String getQualifiedTeams2026() {

		if (cachedQualifiedTeams != null && teamsLastUpdate != null
				&& ChronoUnit.HOURS.between(teamsLastUpdate, LocalDateTime.now()) < 24) {

			return cachedQualifiedTeams;
		}

		String url = BASE_URL + "teams?league=1&season=2022";
		String response = httpHandler.doGetWithAuth(url, AUTH_HEADER, apiKey);

		if (response == null || response.contains("\"results\":0") || response.contains("\"response\":[]")) {

			response = "{\"response\":["
					+ "{\"team\":{\"id\":227,\"name\":\"Estados Unidos\",\"logo\":\"https://media.api-sports.io/football/teams/227.png\"},\"venue\":{\"name\":\"Anfitrión\"}},"
					+ "{\"team\":{\"id\":16,\"name\":\"México\",\"logo\":\"https://media.api-sports.io/football/teams/16.png\"},\"venue\":{\"name\":\"Anfitrión\"}},"
					+ "{\"team\":{\"id\":5519,\"name\":\"Canadá\",\"logo\":\"https://media.api-sports.io/football/teams/5519.png\"},\"venue\":{\"name\":\"Anfitrión\"}},"
					+ "{\"team\":{\"id\":26,\"name\":\"Argentina\",\"logo\":\"https://media.api-sports.io/football/teams/26.png\"},\"venue\":{\"name\":\"Clasificado (CONMEBOL)\"}},"
					+ "{\"team\":{\"id\":8,\"name\":\"Colombia\",\"logo\":\"https://media.api-sports.io/football/teams/8.png\"},\"venue\":{\"name\":\"Clasificado (CONMEBOL)\"}},"
					+ "{\"team\":{\"id\":6,\"name\":\"Brasil\",\"logo\":\"https://media.api-sports.io/football/teams/6.png\"},\"venue\":{\"name\":\"Clasificado (CONMEBOL)\"}},"
					+ "{\"team\":{\"id\":9,\"name\":\"España\",\"logo\":\"https://media.api-sports.io/football/teams/9.png\"},\"venue\":{\"name\":\"Clasificado (UEFA)\"}},"
					+ "{\"team\":{\"id\":67,\"name\":\"Francia\",\"logo\":\"https://media.api-sports.io/football/teams/67.png\"},\"venue\":{\"name\":\"Clasificado (UEFA)\"}},"
					+ "{\"team\":{\"id\":10,\"name\":\"Inglaterra\",\"logo\":\"https://media.api-sports.io/football/teams/10.png\"},\"venue\":{\"name\":\"Clasificado (UEFA)\"}},"
					+ "{\"team\":{\"id\":25,\"name\":\"Alemania\",\"logo\":\"https://media.api-sports.io/football/teams/25.png\"},\"venue\":{\"name\":\"Clasificado (UEFA)\"}},"
					+ "{\"team\":{\"id\":27,\"name\":\"Portugal\",\"logo\":\"https://media.api-sports.io/football/teams/27.png\"},\"venue\":{\"name\":\"Clasificado (UEFA)\"}}"
					+ "]}";
		}

		if (response != null && !response.contains("\"errors\":{")) {
			cachedQualifiedTeams = response;
			teamsLastUpdate = LocalDateTime.now();
		}

		return response;
	}

	public String getTeamFixtures(String countryName) {

		if (countryName == null || countryName.trim().isEmpty()) {
			return "{\"response\":[]}";
		}

		String normalizedCountry = countryName.trim().toLowerCase();
		Integer teamId = TEAM_IDS.get(normalizedCountry);

		if (teamId == null) {
			return "{\"response\":[]}";
		}

		if (apiKey == null || apiKey.isEmpty() || apiKey.contains("$")) {

		} else {
		}

		String url = BASE_URL + "fixtures?team=" + teamId + "&season=2022";

		String response = httpHandler.doGetWithAuth(url, AUTH_HEADER, apiKey);

		return response != null ? response : "{\"response\":[]}";
	}

	public String getLeagues() {
		String url = BASE_URL + "leagues";
		return httpHandler.doGetWithAuth(url, AUTH_HEADER, apiKey);
	}

	public String getTeams(String leagueId, String season) {
		String url = BASE_URL + "teams?league=" + leagueId + "&season=" + season;
		return httpHandler.doGetWithAuth(url, AUTH_HEADER, apiKey);
	}

	public String getPlayers(String teamId, String season) {
		String url = BASE_URL + "players?team=" + teamId + "&season=" + season;
		return httpHandler.doGetWithAuth(url, AUTH_HEADER, apiKey);
	}
}