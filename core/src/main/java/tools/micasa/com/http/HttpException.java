package tools.micasa.com.http;

public final class HttpException extends RuntimeException implements Result {
    public HttpException(Throwable throwable) {
        super(throwable);
    }
}
