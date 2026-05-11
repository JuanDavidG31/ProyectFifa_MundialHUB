package co.edu.unbosque.projectFifaUbosque.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class FootballService {

	@Autowired
	private ExternalHTTPRequestHandler httpClient;

	@Value("${api.football.key}")
	private String apiKey;

	private static final Map<String, Integer> TEAM_IDS = Map.ofEntries(Map.entry("argentina", 26),
			Map.entry("brasil", 6), Map.entry("colombia", 8), Map.entry("francia", 67), Map.entry("españa", 9),
			Map.entry("inglaterra", 10), Map.entry("estados unidos", 227), Map.entry("méxico", 16),
			Map.entry("mexico", 16), Map.entry("alemania", 25), Map.entry("portugal", 27));

	private String cachedQualifiedTeams;
	private LocalDateTime teamsLastUpdate;

	public String getQualifiedTeams2026() {
		if (cachedQualifiedTeams != null && teamsLastUpdate != null
				&& ChronoUnit.HOURS.between(teamsLastUpdate, LocalDateTime.now()) < 24) {
			return cachedQualifiedTeams;
		}

		String response = httpClient.getFromFootballApi("teams?league=1&season=2022", apiKey);

		if (response == null || response.contains("\"errors\":{")) {
			response = "{\"response\":[{\"team\":{\"id\":26,\"name\":\"Argentina\",\"logo\":\"https://media.api-sports.io/football/teams/26.png\"},\"venue\":{\"name\":\"Clasificado\"}}]}";
		} else {
			cachedQualifiedTeams = response;
			teamsLastUpdate = LocalDateTime.now();
		}
		return response;
	}

	public String getWorldCupTeams() {
		return getQualifiedTeams2026();
	}

	public String getTeamFixtures(String countryName) {
		if (countryName == null || countryName.trim().isEmpty())
			return "{\"response\":[]}";

		Integer teamId = TEAM_IDS.get(countryName.trim().toLowerCase());
		if (teamId == null)
			return "{\"response\":[]}";

		String response = httpClient.getFromFootballApi("fixtures?team=" + teamId + "&season=2022", apiKey);
		return response != null ? response : "{\"response\":[]}";
	}

	public String getLeagues() {
		String response = httpClient.getFromFootballApi("leagues", apiKey);
		return response != null ? response : "{\"response\":[]}";
	}

	public String getTeams(String league, String season) {
		String response = httpClient.getFromFootballApi("teams?league=" + league + "&season=" + season, apiKey);
		return response != null ? response : "{\"response\":[]}";
	}

	public String getPlayers(String team, String season) {
		String response = httpClient.getFromFootballApi("players?team=" + team + "&season=" + season, apiKey);
		return response != null ? response : "{\"response\":[]}";
	}
}