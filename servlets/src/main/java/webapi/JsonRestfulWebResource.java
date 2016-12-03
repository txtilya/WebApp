package webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hegel.core.functions.ExceptionalFunction;

import javax.ws.rs.core.Response;
import java.util.Collection;

interface JsonRestfulWebResource {

    ExceptionalFunction<Object, String, JsonProcessingException> toJsonExceptional =
            new ObjectMapper().writer().withDefaultPrettyPrinter()::writeValueAsString;

    default String toJson(Object o) {
        return toJsonExceptional.getOrThrowUnchecked(o);
    }

    default Response ok(Collection<?> objects) {
        return Response.ok(toJson(objects)).build();
    }

    default Response ok(Object o) {
        return Response.ok(toJson(o)).build();
    }

    default Response noContent() {
        return Response.noContent().build();
    }
}