package co.edu.unbosque.projectFifaUbosque.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
public class ExternalHTTPRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExternalHTTPRequestHandler.class);

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10)).build();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final String FOOTBALL_API_BASE_URL = "https://v3.football.api-sports.io/";
	private static final String FOOTBALL_AUTH_HEADER = "x-apisports-key";

	private static final String FOOTBALL_DATA_BASE_URL = "https://api.football-data.org/v4/";
	private static final String FOOTBALL_DATA_AUTH_HEADER = "X-Auth-Token";

	private static final String SERPAPI_BASE_URL = "https://serpapi.com/";
	private static final String QR_API_BASE_URL = "https://veltrixpassqrgen.onrender.com/";

	public String getFromFootballDataApi(String endpoint, String apiKey) {
		return executeGet(FOOTBALL_DATA_BASE_URL + endpoint, FOOTBALL_DATA_AUTH_HEADER, apiKey);
	}

	public String getFromFootballApi(String endpoint, String apiKey) {
		return executeGet(FOOTBALL_API_BASE_URL + endpoint, FOOTBALL_AUTH_HEADER, apiKey);
	}

	public String getFromSerpApi(String queryParams) {
		return executeGet(SERPAPI_BASE_URL + queryParams, null, null);
	}

	public String getFromQrApi(String endpoint) {
		return executeGet(QR_API_BASE_URL + endpoint, null, null);
	}

	private String executeGet(String url, String authHeader, String apiKey) {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().GET().uri(URI.create(url))
				.header("Content-type", "application/json").timeout(Duration.ofSeconds(15));

		if (authHeader != null && apiKey != null) {
			requestBuilder.header(authHeader, apiKey);
		}

		try {
			HttpResponse<String> response = HTTP_CLIENT.send(requestBuilder.build(),
					HttpResponse.BodyHandlers.ofString());
			return response.body();

		} catch (IOException e) {
			logger.error("Error de conexión con API externa en URL: {}", url, e);
		} catch (InterruptedException e) {
			logger.error("La conexión fue interrumpida inesperadamente: {}", url, e);
			Thread.currentThread().interrupt();
		}
		return null;
	}

	public String prettyPrintUsingGson(String uglyJson) {
		try {
			if (uglyJson == null || uglyJson.isBlank())
				return uglyJson;
			JsonElement jsonElement = JsonParser.parseString(uglyJson);
			return GSON.toJson(jsonElement);
		} catch (Exception e) {
			logger.warn("El JSON recibido no pudo ser formateado. Se devuelve como texto plano.");
			return uglyJson;
		}
	}
}