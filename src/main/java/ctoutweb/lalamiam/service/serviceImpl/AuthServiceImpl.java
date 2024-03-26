package ctoutweb.lalamiam.service.serviceImpl;

import ctoutweb.lalamiam.exception.AuthException;
import ctoutweb.lalamiam.model.JwtIssue;
import ctoutweb.lalamiam.model.LoginResponse;
import ctoutweb.lalamiam.model.RegisterResponse;
import ctoutweb.lalamiam.model.dto.LoginDto;
import ctoutweb.lalamiam.model.dto.RegisterDto;
import ctoutweb.lalamiam.repository.entity.UserEntity;
import ctoutweb.lalamiam.repository.transaction.UserTransactionSession;
import ctoutweb.lalamiam.security.authentication.UserPrincipal;
import ctoutweb.lalamiam.security.jwt.JwtIssuer;
import ctoutweb.lalamiam.service.AuthService;
import ctoutweb.lalamiam.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  private final PasswordEncoder passwordEncoder;
  private final UserTransactionSession userTransactionSession;
  private final JwtService jwtService;
  private final JwtIssuer jwtIssuer;
  private final AuthenticationManager authenticationManager;

  public AuthServiceImpl(
          PasswordEncoder passwordEncoder,
          UserTransactionSession userTransactionSession,
          JwtService jwtService,
          JwtIssuer jwtIssuer,
          AuthenticationManager authenticationManager
  ) {
    this.passwordEncoder = passwordEncoder;
    this.userTransactionSession = userTransactionSession;
    this.jwtService = jwtService;
    this.jwtIssuer = jwtIssuer;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public LoginResponse login(LoginDto login) {
    // Todo test
    Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            login.email(), login.password()
    ));

    SecurityContextHolder.getContext().setAuthentication(auth);
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

    // JWT
    JwtIssue jwtIssue = jwtIssuer.issue(userPrincipal);
    jwtService.saveJwt(userPrincipal.getId(), jwtIssue);

    return null;
  }

  @Override
  public RegisterResponse register(RegisterDto registerDto) {
    // Todo test
    // Verification existance Email
    UserEntity findUser = userTransactionSession.getUserInformationByEmail(registerDto.email());
    if(findUser != null) throw new AuthException("Email déja existant", HttpStatus.CONFLICT);

    // Mot de passe hashé
    String passwordHash = passwordEncoder.encode(registerDto.password());

    // Sauvegarde Utilisateur
    UserEntity registerUser = userTransactionSession.registerUser(registerDto.email(), passwordHash);

    return null;
  }
}
