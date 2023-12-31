package osa.ora;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;
import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.model.rest.RestBindingMode;

@ApplicationScoped
public class RestJDBCRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Define the REST API endpoint
        rest("/api")
            .get("/hello/{name}")
                .to("direct:helloRoute");
        
        // Define the REST API endpoint
        rest("/api")
            .get("/user/{id}")
                .to("direct:userRoute");

        // Define the route that handles the parameter and generates the response
        from("direct:helloRoute")
            .log("Received request with name: ${header.name}")
            .setHeader("Content-Type", constant("text/plain"))
            .setBody(simple("Hello, ${header.name}!"));

        // Define the route that handles the parameter and generates the response
        from("direct:userRoute")
            .log("Received request with user ID: ${header.id}")
            .setHeader("Content-Type", constant("application/json"))
            .choice()
                .when(header("id").isEqualTo("1"))
                    .setBody(constant("{ \"name\": \"Osa Ora\", \"age\": 30 }"))
                .when(header("id").isEqualTo("2"))
                    .setBody(constant("{ \"name\": \"Osama Oransa\", \"age\": 35 }"))
                .otherwise()
                    .setHeader("CamelHttpResponseCode", constant(404))
                    .setBody(constant("{ \"error\": \"User not found\" }"));
    }
}
