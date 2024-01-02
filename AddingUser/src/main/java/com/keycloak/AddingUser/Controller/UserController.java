package com.keycloak.AddingUser.Controller;

import com.keycloak.AddingUser.Config.KeycloakProvider;
import com.keycloak.AddingUser.Model.CreateUserRequest;
import com.keycloak.AddingUser.Model.LoginRequest;
import com.keycloak.AddingUser.Service.KeycloakAdminClientService;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {
    private final KeycloakAdminClientService kcAdminClient;

    private final KeycloakProvider kcProvider;

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(UserController.class);


    public UserController(KeycloakAdminClientService kcAdminClient, KeycloakProvider kcProvider) {
        this.kcProvider = kcProvider;
        this.kcAdminClient = kcAdminClient;
    }


    @PostMapping(value = "/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
        Response createdResponse = kcAdminClient.createKeycloakUser(user);
        return ResponseEntity.status(createdResponse.getStatus()).build();

    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody LoginRequest loginRequest) {
         Keycloak keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getUsername(), loginRequest.getPassword()).build();

        AccessTokenResponse accessTokenResponse = null;
        try {
//            accessTokenResponse = KeycloakBuilder.builder()
//                    .serverUrl("http://localhost:8080/")
//                    .realm("check")
//                    .grantType(OAuth2Constants.PASSWORD)
//                    .clientId("checkingRealm")
//                    .clientSecret("UkCVCdmvO0ixqL9fkA8A0aQczXmoWpSE")
//                    .username(loginRequest.getUsername())
//                    .password(loginRequest.getPassword())
//                    .build()
//                    .tokenManager()
//                    .getAccessToken();

          accessTokenResponse = keycloak.tokenManager().getAccessToken();
           return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse);
        } catch (BadRequestException ex) {
            LOG.warn("invalid account....details--{}", ex.getResponse().readEntity(String.class), ex);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);
        }
    }



}




