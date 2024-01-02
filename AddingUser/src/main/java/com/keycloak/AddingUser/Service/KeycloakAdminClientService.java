package com.keycloak.AddingUser.Service;

import com.keycloak.AddingUser.Config.KeycloakProvider;
import com.keycloak.AddingUser.Model.CreateUserRequest;
import com.keycloak.AddingUser.Model.User;
import com.keycloak.AddingUser.Repo.UserRepository;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;


@Service
public class KeycloakAdminClientService {
    @Value("${keycloak.realm}")
    public String realm;

    @Autowired
    UserRepository userRepository;

    private final KeycloakProvider kcProvider;


    public KeycloakAdminClientService(KeycloakProvider keycloakProvider) {
        this.kcProvider = keycloakProvider;
    }

    public Response createKeycloakUser(CreateUserRequest user) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstname());
        kcUser.setLastName(user.getLastname());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        Response response = usersResource.create(kcUser);

        if (response.getStatus() == 201) {
           // If you want to save the user to your other database, do it here, for example:
            User localUser = new User();
            localUser.setFirstname(kcUser.getFirstName());
            localUser.setLastname(kcUser.getLastName());
            localUser.setEmail(user.getEmail());
           // localUser.setCreatedate(Timestamp.from(Instant.now()));
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            usersResource.get(userId).sendVerifyEmail();
            userRepository.save(localUser);
        }

        return response;
    }
    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

}