package me.trouper.sentinel.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.trouper.sentinel.data.misc.IPLocation;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class IPUtils {
    
    public static String extractIp(InetAddress e){
        if(e == null)
            return "";
        return e.getHostAddress().replaceAll("\\.",".");
    }

    public static IPLocation getLocation(String ip) {
        JsonObject ipInfo = new JsonObject();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json/%s?fields=17563647".formatted(ip)))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ipInfo = JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return getLocation(ipInfo);
    }

    public static IPLocation getLocation(JsonObject ipInfo) {
        String country = getStringOrNull(ipInfo, "country");
        String countryCode = getStringOrNull(ipInfo, "countryCode");
        String region = getStringOrNull(ipInfo, "regionName");
        String regionCode = getStringOrNull(ipInfo, "region");
        String city = getStringOrNull(ipInfo, "city");
        String district = getStringOrNull(ipInfo, "district");
        String zip = getStringOrNull(ipInfo, "zip");
        String lat = getStringOrNull(ipInfo, "lat");
        String lon = getStringOrNull(ipInfo, "lon");
        String timezone = getStringOrNull(ipInfo, "timezone");
        String isp = getStringOrNull(ipInfo, "isp");
        String org = getStringOrNull(ipInfo, "org");
        String as = getStringOrNull(ipInfo, "as");
        String reverse = getStringOrNull(ipInfo, "reverse");
        boolean mobile = getBooleanOrFalse(ipInfo, "mobile");
        boolean proxy = getBooleanOrFalse(ipInfo, "proxy");
        boolean hosting = getBooleanOrFalse(ipInfo, "hosting");

        return new IPLocation(country, countryCode, region, regionCode, city,district, zip, lat, lon, timezone, isp, org, as,reverse,mobile,proxy,hosting);
    }

    private static String getStringOrNull(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return element != null ? element.getAsString() : "null";
    }
    private static boolean getBooleanOrFalse(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return element != null && element.getAsBoolean();
    }
}
