package co.edu.unbosque.projectFifaUbosque.service;

import co.edu.unbosque.projectFifaUbosque.dto.PlayerStatDTO;
import co.edu.unbosque.projectFifaUbosque.dto.TicketMatchDTO;
import co.edu.unbosque.projectFifaUbosque.repository.TicketRepository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchService {

	@Autowired
	private ExternalHTTPRequestHandler httpHandler;

	@Autowired
	private TicketRepository ticketRepository;

	@Value("${football.api.key}")
	private String apiKey;

	private Map<String, String> teamCrestsCache = new HashMap<>();

	public List<PlayerStatDTO> getTopScorers() {
		String url = "https://api.football-data.org/v4/competitions/CL/scorers?limit=15";
		String jsonResponse = httpHandler.doGetWithAuth(url, "X-Auth-Token", apiKey);
		List<PlayerStatDTO> scorers = new ArrayList<>();

		if (jsonResponse == null || jsonResponse.isBlank())
			return scorers;

		try {
			JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
			JsonArray scorersArray = root.getAsJsonArray("scorers");

			for (JsonElement elem : scorersArray) {
				JsonObject s = elem.getAsJsonObject();
				JsonObject player = s.getAsJsonObject("player");
				JsonObject team = s.getAsJsonObject("team");

				String playerName = player.has("name") && !player.get("name").isJsonNull()
						? player.get("name").getAsString()
						: "Desconocido";
				String teamName = team.has("name") && !team.get("name").isJsonNull() ? team.get("name").getAsString()
						: "Sin Equipo";
				String crestUrl = team.has("crest") && !team.get("crest").isJsonNull() ? team.get("crest").getAsString()
						: "";

				int goals = s.has("goals") && !s.get("goals").isJsonNull() ? s.get("goals").getAsInt() : 0;

				scorers.add(new PlayerStatDTO(playerName, teamName, crestUrl, goals));
			}
		} catch (Exception e) {
			System.err.println("Error procesando goleadores: " + e.getMessage());
		}
		return scorers;
	}

	public List<PlayerStatDTO> getTopAssists() {
		String url = "https://api.football-data.org/v4/competitions/CL/scorers?limit=30";
		String jsonResponse = httpHandler.doGetWithAuth(url, "X-Auth-Token", apiKey);
		List<PlayerStatDTO> assistsList = new ArrayList<>();

		if (jsonResponse == null || jsonResponse.isBlank())
			return assistsList;

		try {
			JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
			JsonArray scorersArray = root.getAsJsonArray("scorers");

			for (JsonElement elem : scorersArray) {
				JsonObject s = elem.getAsJsonObject();

				int assists = s.has("assists") && !s.get("assists").isJsonNull() ? s.get("assists").getAsInt() : 0;

				if (assists > 0) {
					JsonObject player = s.getAsJsonObject("player");
					JsonObject team = s.getAsJsonObject("team");

					String playerName = player.has("name") && !player.get("name").isJsonNull()
							? player.get("name").getAsString()
							: "Desconocido";
					String teamName = team.has("name") && !team.get("name").isJsonNull()
							? team.get("name").getAsString()
							: "Sin Equipo";
					String crestUrl = team.has("crest") && !team.get("crest").isJsonNull()
							? team.get("crest").getAsString()
							: "";

					assistsList.add(new PlayerStatDTO(playerName, teamName, crestUrl, assists));
				}
			}

			assistsList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

		} catch (Exception e) {
			System.err.println("Error procesando asistencias: " + e.getMessage());
		}

		return assistsList.stream().limit(10).collect(Collectors.toList());
	}

	private Map<String, String> getTeamCrests() {
		if (!teamCrestsCache.isEmpty()) {
			return teamCrestsCache;
		}

		String url = "https://api.football-data.org/v4/competitions/WC/teams";
		String jsonResponse = httpHandler.doGetWithAuth(url, "X-Auth-Token", apiKey);

		if (jsonResponse != null && !jsonResponse.isBlank()) {
			try {
				JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
				if (root.has("teams")) {
					for (JsonElement elem : root.getAsJsonArray("teams")) {
						JsonObject team = elem.getAsJsonObject();
						String name = team.get("name").getAsString();
						String crest = team.has("crest") && !team.get("crest").isJsonNull()
								? team.get("crest").getAsString()
								: "";
						teamCrestsCache.put(name, crest);
					}
				}
			} catch (Exception e) {
				System.err.println("Error descargando banderas: " + e.getMessage());
			}
		}
		return teamCrestsCache;
	}

	public List<TicketMatchDTO> getWcMatches() {
		String url = "https://api.football-data.org/v4/competitions/WC/matches";

		String jsonResponse = httpHandler.doGetWithAuth(url, "X-Auth-Token", apiKey);

		List<TicketMatchDTO> matches = new ArrayList<>();
		if (jsonResponse == null || jsonResponse.isBlank())
			return matches;

		try {
			JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
			if (!root.has("matches"))
				return matches;

			JsonArray matchesArray = root.getAsJsonArray("matches");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm")
					.withZone(ZoneId.systemDefault());

			Map<String, String> crests = getTeamCrests();

			List<Object[]> ventasBrutas = ticketRepository.countAllTicketsGroupedByMatch();

			Map<String, Long> ventasPorPartido = ventasBrutas.stream()
					.collect(Collectors.toMap(fila -> (String) fila[0], fila -> (Long) fila[1]));

			for (JsonElement elem : matchesArray) {
				JsonObject m = elem.getAsJsonObject();
				TicketMatchDTO dto = new TicketMatchDTO();

				dto.setId(m.get("id").getAsLong());

				JsonObject home = m.has("homeTeam") ? m.getAsJsonObject("homeTeam") : null;
				JsonObject away = m.has("awayTeam") ? m.getAsJsonObject("awayTeam") : null;

				String localName = home != null && home.has("name") && !home.get("name").isJsonNull()
						? home.get("name").getAsString()
						: "TBD";
				String awayName = away != null && away.has("name") && !away.get("name").isJsonNull()
						? away.get("name").getAsString()
						: "TBD";

				dto.setPrecio(64 + (int) (dto.getId() % 150));

				String matchNameStr = localName + " VS " + awayName;
				long boletosVendidos = ventasPorPartido.getOrDefault(matchNameStr, 0L);

				long MAX_BOLETOS = 50;

				dto.setLocal(localName);
				dto.setVisitante(awayName);

				dto.setLocalCrest(crests.getOrDefault(localName, ""));
				dto.setVisitanteCrest(crests.getOrDefault(awayName, ""));

				String utcDate = m.get("utcDate").getAsString();
				dto.setFecha(formatter.format(Instant.parse(utcDate)));

				String group = m.has("group") && !m.get("group").isJsonNull() ? m.get("group").getAsString()
						: "Fase Final";
				if (group.startsWith("GROUP_")) {
					group = group.replace("GROUP_", "Grupo ");
				} else if (group.equals("Fase Final")) {
					if (m.has("stage") && !m.get("stage").isJsonNull()) {
						group = m.get("stage").getAsString().replace("_", " ");
					}
				}
				dto.setGrupo(group);

				dto.setEstadio("Estadio Mundialista");
				dto.setPrecio(120 + (int) (dto.getId() % 150));

				if (boletosVendidos >= MAX_BOLETOS) {
					dto.setEstado("Agotado");
				} else if (boletosVendidos >= MAX_BOLETOS - 5) {
					dto.setEstado("Pocos boletos");
				} else {
					dto.setEstado("Disponible");
				}

				matches.add(dto);
			}
		} catch (Exception e) {
			System.err.println("Error parseando partidos del mundial: " + e.getMessage());
		}

		return matches;
	}

	// NUEVO MÉTODO PARA OBTENER TODOS LOS PARTIDOS (EN VIVO, TERMINADOS,
	// PROGRAMADOS)
	public List<Map<String, Object>> getAllLiveMatches() {
		// NOTA: Aquí está "CL". Cuando empiece el mundial, cámbialo a "WC"
		String url = "https://api.football-data.org/v4/competitions/PD/matches";
		String response = httpHandler.doGetWithAuth(url, "X-Auth-Token", apiKey);

		JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
		JsonArray matchesArray = jsonObject.getAsJsonArray("matches");

		List<Map<String, Object>> result = new ArrayList<>();

		for (JsonElement el : matchesArray) {
			JsonObject m = el.getAsJsonObject();
			Map<String, Object> matchData = new HashMap<>();

			matchData.put("id", m.get("id").getAsLong());
			matchData.put("utcDate", m.get("utcDate").getAsString());
			matchData.put("status", m.get("status").getAsString());
			matchData.put("stage", m.has("stage") && !m.get("stage").isJsonNull() ? m.get("stage").getAsString() : "");

			JsonObject homeTeam = m.getAsJsonObject("homeTeam");
			matchData.put("homeTeam",
					homeTeam.has("name") && !homeTeam.get("name").isJsonNull() ? homeTeam.get("name").getAsString()
							: "Por definir");
			matchData.put("homeCrest",
					homeTeam.has("crest") && !homeTeam.get("crest").isJsonNull() ? homeTeam.get("crest").getAsString()
							: "https://crests.football-data.org/764.svg"); // Escudo por defecto

			JsonObject awayTeam = m.getAsJsonObject("awayTeam");
			matchData.put("awayTeam",
					awayTeam.has("name") && !awayTeam.get("name").isJsonNull() ? awayTeam.get("name").getAsString()
							: "Por definir");
			matchData.put("awayCrest",
					awayTeam.has("crest") && !awayTeam.get("crest").isJsonNull() ? awayTeam.get("crest").getAsString()
							: "https://crests.football-data.org/764.svg");

			// Procesar el Marcador (Score)
			JsonObject score = m.getAsJsonObject("score");
			Map<String, Object> scoreData = new HashMap<>();
			scoreData.put("duration",
					score.has("duration") && !score.get("duration").isJsonNull() ? score.get("duration").getAsString()
							: "REGULAR");

			scoreData.put("fullTime", parseScoreObj(score.getAsJsonObject("fullTime")));
			scoreData.put("halfTime", parseScoreObj(score.getAsJsonObject("halfTime")));
			scoreData.put("extraTime", parseScoreObj(score.getAsJsonObject("extraTime")));
			scoreData.put("penalties", parseScoreObj(score.getAsJsonObject("penalties")));

			matchData.put("score", scoreData);
			result.add(matchData);
		}
		return result;
	}

	// Función auxiliar para leer los goles de forma segura
	private Map<String, Integer> parseScoreObj(JsonObject obj) {
		Map<String, Integer> s = new HashMap<>();
		if (obj != null && !obj.isJsonNull()) {
			s.put("home", obj.has("home") && !obj.get("home").isJsonNull() ? obj.get("home").getAsInt() : null);
			s.put("away", obj.has("away") && !obj.get("away").isJsonNull() ? obj.get("away").getAsInt() : null);
		} else {
			s.put("home", null);
			s.put("away", null);
		}
		return s;
	}
}