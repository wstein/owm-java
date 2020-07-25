package com.avaya.global.wstein.owm;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

public class Application {
  private final String api_key;

  public Application(String api_key) {
    this.api_key = api_key;
  }

  public static void main(String[] args) {
    String city = "Berlin";

    if (args.length == 1) {
      city = args[0];
    }

    Application application = new Application("eeeed3fcd993153a39d003d1b656e8a3");

    HttpResponse<JsonNode> response = application.requestWeatherByCity(city);

    if (response.getStatus() == 200) {
      application.printResponse(response);
    } else {
      application.exitWithError(response);
    }
  }

  private HttpResponse<JsonNode> requestWeatherByCity(String city) {
    return Unirest.get("https://api.openweathermap.org/data/2.5/weather")
            .header("accept", "application/json")
            .queryString("q", city)
            .queryString("units", "metric")
            .queryString("appid", api_key)
            .asJson();
  }

  private void printResponse(HttpResponse<JsonNode> response) {
    JSONObject data = response.getBody().getObject();
    System.out.printf("%s: %.2f°C%n", data.getString("name"), data.getJSONObject("main").getFloat("temp"));
    System.out.printf("%s%n", data.getJSONArray("weather").getJSONObject(0).getString("description"));
    System.out.printf("Clouds: %d%%%n", data.getJSONObject("clouds").getInt("all"));
    System.out.printf("Humidity: %d%%%n", data.getJSONObject("main").getInt("humidity"));
    System.out.printf("Wind: %.2f m/s%n", data.getJSONObject("wind").getFloat("speed"));
    System.out.printf("Pressure: %dhpa%n", data.getJSONObject("main").getInt("pressure"));
    System.out.printf("https://openweathermap.org/city/%d%n", data.getInt("id"));
  }

  private void exitWithError(HttpResponse<JsonNode> response) {
    System.out.printf("Status %d: %s%n", response.getStatus(), response.getStatusText());
    System.exit(response.getStatus());
  }
}
