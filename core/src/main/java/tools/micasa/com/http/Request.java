package tools.micasa.com.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static tools.micasa.com.Utils.*;
import static java.util.stream.Collectors.*;

enum Method {
    GET, POST, PUT, DELETE, PATCH
}

public record Request(HttpClient.Version version, Method method, Duration timeout, String uri, Map<String, String> headers, String body) {

    public static Request GET(String uri, Map<String, String> headers) {
        return new Request(HttpClient.Version.HTTP_2, Method.GET, Duration.ofSeconds(30), uri, headers, null);
    }

    public static Request DELETE(String uri, Map<String, String> headers) {
        return new Request(HttpClient.Version.HTTP_2, Method.DELETE, Duration.ofSeconds(30), uri, headers, null);
    }

    public static Request POST(String uri, Map<String, String> headers, String body) {
        return new Request(HttpClient.Version.HTTP_2, Method.POST, Duration.ofSeconds(30), uri, headers, body);
    }

    public static Request PUT(String uri, Map<String, String> headers, String body) {
        return new Request(HttpClient.Version.HTTP_2, Method.PUT, Duration.ofSeconds(30), uri, headers, body);
    }

    public static Request PATCH(String uri, Map<String, String> headers, String body) {
        return new Request(HttpClient.Version.HTTP_2, Method.PATCH, Duration.ofSeconds(30), uri, headers, body);
    }

    public Request withEnv(Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) return this;
        var newUri = replace(uri, variables);
        var newHeaders = headers == null || headers.isEmpty()
            ? headers
            : headers()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> replace(entry.getValue(), variables)));
        return new Request(this.version, this.method, this.timeout, newUri, newHeaders, this.body);
    }

    public Request withData(Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) return this;
        var newUri = replace(uri, variables);
        var newBody = body != null ? replace(body, variables) : null;
        return new Request(this.version, this.method, this.timeout, newUri, headers, newBody);
    }

    public HttpRequest toHttpRequest() {
        var requestBuilder = HttpRequest
                .newBuilder()
                .version(version)
                .timeout(timeout)
                .uri(URI.create(uri))
                .method(method.name(), body == null ? noBody() : HttpRequest.BodyPublishers.ofString(body));

        if (headers != null) {
            headers
                .entrySet()
                .stream()
                .forEach(entry -> requestBuilder.header(entry.getKey(), entry.getValue()));
        };

        return requestBuilder.build();
    }

}
