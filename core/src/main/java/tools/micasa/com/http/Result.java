package tools.micasa.com.http;

public sealed interface Result permits Response, HttpException {
}
