package tools.micasa.com.http;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.util.stream.Collectors.*;

/**
 *
 * @param version the HTTP version
 * @param uri the HTTP request URI
 * @param statusCode the HTTP response status
 * @param headers the response headers. Even though multiple headers with the same name are allowed, they are combined as described in the HTTP specification:
 *    A recipient MAY combine multiple header fields with the same field
 *    name into one "field-name: field-value" pair, without changing the
 *    semantics of the message, by appending each subsequent field value to
 *    the combined field value in order, separated by a comma.
 * @param body the HTTP response body
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7230#section-3.2.2">Hypertext Transfer Protocol (HTTP/1.1): Message Syntax and Routing</a>
 */
public record Response(HttpClient.Version version, String uri, int statusCode, Map<String, String> headers, String body) implements Result {

    public Response(String uri, int statusCode, Map<String, String> headers, String body) {
        this(HttpClient.Version.HTTP_2, uri, statusCode, headers, body);
    }

    public static Response fromHttpResponse(HttpResponse response) {
        return new Response(
            response.version(),
            response.uri().toString(),
            response.statusCode(),
            response.headers().map()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().stream().collect(joining(",")))),
            response.body().toString()
        );
    }

}
