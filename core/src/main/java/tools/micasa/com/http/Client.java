package tools.micasa.com.http;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.concurrent.Executors.*;

public class Client {

    //  TODO: configure the client
    HttpClient client = HttpClient
        .newBuilder()
        .executor(newVirtualThreadPerTaskExecutor())
        .build();

    public Stream<Result> execute(Request request, Map<String, String> environmentVariables, Stream<Map<String, String>> dataStream) {
        var newRequest = request.withEnv(environmentVariables);
        return dataStream
            .parallel()
            .map(newRequest::withData)
            .map(Request::toHttpRequest)
            .map(this::sendRequest);
    }

    private Result sendRequest(HttpRequest request) {
        try {
            return Response.fromHttpResponse(client.send(request, HttpResponse.BodyHandlers.ofString()));
        } catch (Exception e) {
            return new HttpException(e);
        }
    }

}
