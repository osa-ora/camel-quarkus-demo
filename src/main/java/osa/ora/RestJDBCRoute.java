package osa.ora;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestJDBCRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Define REST API endpoint
        rest("/users")
            .get("/{id}")
            .to("direct:getUserAccount");

        // Define the route to retrieve user account details from the database
        from("direct:getUserAccount")
            .setHeader("Content-Type", constant("application/json"))
            .bean(DatabaseService.class, "getUserAccount") // Call the custom bean here
            .to("jdbc:camel")
            .choice()
                .when(simple("${body.size()} > 0"))
                    .transform().jsonpath("$[0]")
                    .setHeader("CamelHttpResponseCode", constant(200))
                .otherwise()
                    .setHeader("CamelHttpResponseCode", constant(404))
                    .setBody(constant("{ \"error\": \"User account not found\" }"))
            .end()
            .convertBodyTo(String.class) // Convert the response body to a string
            .log("User account details: ${body}");
    }
    //This bean for DB query preparation, it can return DB query per DB type ..
    public static class DatabaseService {
        public void getUserAccount(@Header("id") String id, Exchange exchange) {
            // Existing logic to fetch user account details from the database
            exchange.getIn().setBody("select * from account where id = " + id + " LIMIT 1");
            exchange.getIn().setHeader("CamelSqlQuery", true);
        }
    }
}
