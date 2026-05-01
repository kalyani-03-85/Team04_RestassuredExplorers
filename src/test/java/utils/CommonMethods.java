package utils;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.*;
public class CommonMethods {

    RequestSpecification request;
    Response response;

    // ---------------- SET AUTH ----------------

    public RequestSpecification setAuth(String authType) {

        request = given();

        switch (authType.toLowerCase()) {

            case "valid token":
                request.header("Authorization", "Bearer validToken");
                break;

            case "invalid token":
                request.header("Authorization", "Bearer invalidToken");
                break;

            case "expired token":
                request.header("Authorization", "Bearer expiredToken");
                break;

            case "no auth":
                // no header
                break;
        }

        return request;
    }


    // ---------------- SEND REQUEST ----------------

    public Response sendRequest(RequestSpecification req, String method, String endpoint) {

        String url;

        if (endpoint.equalsIgnoreCase("valid endpoint")) {
            url = "/logout";
        } else {
            url = "/invalid-endpoint";
        }

        switch (method.toUpperCase()) {

            case "GET":
                return req.get(url);

            case "POST":
                return req.post(url);

            case "PUT":
                return req.put(url);

            case "DELETE":
                return req.delete(url);

            default:
                throw new RuntimeException("Invalid method: " + method);
        }
    
}
}