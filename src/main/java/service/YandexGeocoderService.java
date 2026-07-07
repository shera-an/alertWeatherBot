package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.YandexGeocoderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.interfaces.GeocoderService;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class YandexGeocoderService implements GeocoderService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(YandexGeocoderService.class);

    public double[] getCoordinates(String city) throws IOException, InterruptedException{
        String url = String.format(
                YandexGeocoderConfig.getYgeocoderUrl() + "?apikey="
                 + YandexGeocoderConfig.getYgeocoderKey() + "&geocode=%s&format=json",
                URLEncoder.encode(city, StandardCharsets.UTF_8)

        );
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        logger.info("Запрос к геокодеру: {}", url);

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200){
            throw new IOException("Ошибка геокодера: " + response.statusCode());
        }
        logger.info("Cтатус ответа: {}", response.statusCode());
        logger.info("Тело ответа: {}", response.body());


        JsonNode root = objectMapper.readTree(response.body());
        JsonNode posNode = root
                .path("response")
                .path("GeoObjectCollection")
                .path("featureMember")
                .path(0)
                .path("GeoObject")
                .path("Point")
                .path("pos");

        if(posNode.isMissingNode()){
            throw new IOException("Город не найден: " + city);
        }

        String[] coordinates = posNode.asText().split(" ");
        double lon = Double.parseDouble(coordinates[0]);
        double lat = Double.parseDouble(coordinates[1]);
        logger.info("Координаты: lat= {}, lon= {}", lat,lon);

        return new double[]{lat, lon};
    }
    public String getCityNameByCoords(double lat, double lon) {
        try {
            String url = String.format(
                    "https://geocode-maps.yandex.ru/v1?apikey=%s&geocode=%s,%s&format=json",
                    config.YandexGeocoderConfig.getYgeocoderKey(),
                    lon, lat
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(response.body());
            String address = root
                    .path("response")
                    .path("GeoObjectCollection")
                    .path("featureMember")
                    .path(0)
                    .path("GeoObject")
                    .path("metaDataProperty")
                    .path("GeocoderMetaData")
                    .path("text")
                    .asText();

            return address.isEmpty() ? "ваше местоположение" : address;

        } catch (Exception e) {
            logger.error("Не удалось определить город: ",e);
            return "ваше местоположение";
        }
    }
}
