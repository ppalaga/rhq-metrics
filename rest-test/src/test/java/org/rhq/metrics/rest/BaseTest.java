package org.rhq.metrics.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

@RunWith(Arquillian.class)
public class BaseTest {

    @Deployment(testable=false)
    public static WebArchive createDeployment() {
        File pomFile = new File("../rest-servlet/pom.xml");
        System.out.println("pomfile " + pomFile.getAbsolutePath());
        System.out.flush();

        WebArchive archive =
        ShrinkWrap.create(MavenImporter.class)
            .offline()
            .loadPomFromFile(pomFile)
            .importBuildOutput()
            .as(WebArchive.class);
        System.out.println("archive is " + archive.toString(false));
        System.out.flush();
        return archive;
    }

    @ArquillianResource
    private URL baseUrl;

    @Test
    public void pingTest() throws Exception {
        Response jsonp = given()
                .expect()
                    .statusCode(200)
                .when()
                    .post(new URL(baseUrl, "/rhq-metrics/ping"))
                .then()
                    .contentType(ContentType.JSON)
                .extract()
                    .response();

        JsonPath jsonPath = new JsonPath(jsonp.asString());

        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date date = df.parse(jsonPath.getString("pong"));
        Date now = new Date();

        long timeDifference = now.getTime() - date.getTime();

        Assert.assertTrue("Difference is " + timeDifference, timeDifference < 2500L);
    }

    @Test
    public void testAddGetValue() throws Exception {

        Map<String,Object> data = new HashMap<>();
        String id = "foo";
        data.put("id", id);
        long now = System.currentTimeMillis();
        data.put("timestamp", now);
        data.put("value",42d);

        given()
            .body(data)
            .pathParam("id",id)
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
        .when()
            .post(new URL(baseUrl, "/rhq-metrics/metrics/{id}"));


        given()
            .pathParam("id", id)
          .header("Accepts", "application/json")
        .expect()
           .statusCode(200)
            .log().ifError()
            .body("timestamp[0]", equalTo(now))
        .when()
           .get(new URL(baseUrl, "/rhq-metrics/metrics/{id}"));
    }
}
