package tools.micasa.com.http;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.ConnectException;
import java.util.*;
import java.util.stream.*;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ClientTest {

    private static final String baseUrl = "http://localhost:8080";
    private Client client = new Client();

    @RegisterExtension
    static WireMockExtension wm =
            WireMockExtension.newInstance()
                    .options(wireMockConfig().port(8080))
                    .failOnUnmatchedRequests(true)
                    .build();

    @Test
    @DisplayName("single request")
    void singleRequest() {
        Request r1 = Request.GET(baseUrl + "/customer/{{customerId}}", Map.of("API_KEY", "{{key}}"));
        var env = Map.of("key", "1111");
        var data = Map.of("customerId", "1");

        stubFor(
            get("/customer/1")
                .withHeader("API_KEY", equalTo("1111"))
            .willReturn(ok("response"))
        );

        var result = client.execute(r1, env, Stream.of(data)).toList();

        assertThat(result.size(), is(1));
        assertThat(((Response)result.get(0)).uri(), is(baseUrl + "/customer/1"));
        assertThat(((Response)result.get(0)).statusCode(), is(200));
        assertThat(((Response)result.get(0)).body(), is("response"));
    }

    @Test
    @DisplayName("multiple requests")
    void multipleRequests() {
        Request r1 = Request.GET(baseUrl + "/customer/{{customerId}}", Map.of("API_KEY", "{{key}}"));
        var env = Map.of("key", "1111");
        var data = List.of(Map.of("customerId", "1"), Map.of("customerId", "2"));

        stubFor(
            get(urlMatching("^/customer/[1-2]$"))
                .withHeader("API_KEY", equalTo("1111"))
            .willReturn(ok("response"))
        );

        var result = client.execute(r1, env, data.stream()).toList();

        assertThat(result.size(), is(2));
        assertThat(((Response)result.get(0)).uri(), is(baseUrl + "/customer/1"));
        assertThat(((Response)result.get(1)).uri(), is(baseUrl + "/customer/2"));
    }

    @Test
    @DisplayName("1000 requests")
    void manyRequests() {
        Request r1 = Request.GET(baseUrl + "/customer/{{customerId}}", Map.of("API_KEY", "{{key}}"));
        var env = Map.of("key", "1111");
        var data = IntStream
                    .range(0, 1000)
                    .mapToObj(id -> Map.of("customerId", "" + id));

        stubFor(
            get(urlMatching("^/customer/\\d{1,3}$"))
                .withHeader("API_KEY", equalTo("1111"))
            .willReturn(
                ok("response")
                .withFixedDelay(100)
            )
        );

        var result = client.execute(r1, env, data).toList();

        assertThat(result.size(), is(1000));
        IntStream
            .range(0, 1000)
            .forEach(i -> assertThat(((Response)result.get(i)).uri(), is(baseUrl + "/customer/" + i)));
    }

    @Test
    @DisplayName("connection exception")
    void exception() {
        Request r1 = Request.GET("http://{{baseUri}}/customer/{{customerId}}", null);
        var env = Map.of("baseUri", "domain");
        var data = Map.of("customerId", "1");

        stubFor(
            get("/customer/1")
                .withHeader("API_KEY", equalTo("1111"))
            .willReturn(ok("response"))
        );

        var result = client.execute(r1, env, Stream.of(data)).toList();

        assertThat(result.size(), is(1));
        assertThat(result.get(0), instanceOf(HttpException.class));
        assertThat(((Exception)result.get(0)).getCause(), instanceOf(ConnectException.class));
    }

}
