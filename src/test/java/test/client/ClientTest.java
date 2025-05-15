package test.client;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Properties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import test.ejb.CallbackIF;
import test.ejb.StatelessBean;
import test.servlet.TestServlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ArquillianExtension.class)
public class ClientTest {

    @Deployment
    public static EnterpriseArchive createDeploymentVehicle() {
        return ShrinkWrap.create(EnterpriseArchive.class, "application.ear")
                  .addAsManifestResource(ClientTest.class.getResource("/application.xml"), "application.xml")
                  .addAsModule(
                      ShrinkWrap.create(WebArchive.class, "client_war.war")
                                .addClasses(
                                    TestServlet.class,
                                    CallbackIF.class,
                                    StatelessBean.class)
                                );

    }

    @Test
    @RunAsClient
    public void testSendObjectStream() throws Exception {
        // Send the request and get the response
        HttpResponse<String> response =
            HttpClient.newHttpClient()
                      .send(
                         HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/client-root/servlet_vehicle"))
                                    .GET().build(),
                         HttpResponse.BodyHandlers.ofString());

        // Print the response status code and body
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
        assertEquals(200, response.statusCode());

        Optional<String> xAppNameOpt = response.headers().firstValue("X-AppName");
        assertTrue(xAppNameOpt.isPresent());
        System.out.println("X-AppName: " + xAppNameOpt.get());

        assertEquals("descriptor-appname", xAppNameOpt.get());
    }
}
