package osa.ora;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestJDBCRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Define the REST API endpoint
        rest("/api")
            .get("/user/{id}")
                .to("direct:userRoute");

        // Define the route that handles the parameter and generates the response
        from("direct:userRoute")
            .log("Received request with user ID: ${header.id}")
            .setHeader("Content-Type", constant("application/json"))
            .log("Response: ${body}");
    }
}
