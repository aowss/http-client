package utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockUtils {
    public static void mock(WireMockServer wireMockServer, String url, Map<String, String> headers, int statusCode, String body) {
        MappingBuilder builder = get(urlEqualTo(url));
        if (headers != null && !headers.isEmpty()) {
            headers
                .entrySet()
                .stream()
                .forEach(entry -> builder.withHeader(entry.getKey(), equalTo(entry.getValue())));
        }

        ResponseDefinitionBuilder responseBuilder = aResponse().withStatus(statusCode).withFixedDelay(1000);
        if (body != null) responseBuilder.withBody(body);
        builder.willReturn(responseBuilder);

        wireMockServer.stubFor(builder);
    }
}
