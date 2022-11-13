package tools.micasa.com.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RequestTest {
    @Test
    @DisplayName("environment variables are substituted")
    void env() {
        Request request = Request.GET("http://{{baseUri}}/customer/{{customerId}}", null);
        var env = Map.of("baseUri", "domain");
        Request expectedRequest = Request.GET("http://domain/customer/{{customerId}}", null);
        assertThat(request.withEnv(env), is(expectedRequest));
    }
}
