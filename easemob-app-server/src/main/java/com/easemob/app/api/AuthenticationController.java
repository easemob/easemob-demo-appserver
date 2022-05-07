package com.easemob.app.api;

import com.easemob.app.model.AuthRequest;
import com.easemob.app.model.AuthResponse;
import com.easemob.app.model.VirgilTokenResponse;
import com.easemob.app.service.AuthenticationService;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

  @Autowired
  AuthenticationService authService;

  @PostMapping("/authenticate")
  public ResponseEntity<AuthResponse> login(@RequestBody
          AuthRequest authRequest) {
    String authToken = authService.login(authRequest.getIdentity());

    return new ResponseEntity<>(new AuthResponse(authToken), HttpStatus.OK);
  }

  @GetMapping("/virgil-jwt")
  public ResponseEntity<VirgilTokenResponse> getVirgilToken(
      @RequestHeader(name = "Authorization", required = false) String authToken)
      throws CryptoException {
    String identity = authService.getIdentity(authToken);

    if (identity == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    Jwt token = authService.generateVirgilToken(identity);
    return new ResponseEntity<>(new VirgilTokenResponse(token.stringRepresentation()),
                                HttpStatus.OK);
  }
}
