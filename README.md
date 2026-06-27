# QuickBite

- **quick bite** is a production grade food ordering app like **talabat**.

## Key Points

### What makes quick bite a production grade app? see the following.

- Global app config that parses all the environment variables and provides
  grouping for related variables, and prevents working for strings to access environment variables, typing mistakes as
  it makes use of auto-complete
  for strictly types properties.

```java
@ConfigurationProperties(prefix = "app")
public record AppConfig(
        int serverPort,
        AppEnvironment environment,
        DbConfig db,
        JwtConfig jwt,
        PasswordEncoder passwordEncoder,
        Cookies cookies
) {
    public record DbConfig(
            String host,
            int port,
            String username,
            @NotBlank String password,
            @NotBlank String name,
            int poolMin,
            int poolMax,
            @NotBlank String migrationDirectory,
            @NotBlank String migrationExtension
    ) {
    }
    // other configs
}
```

```java
// to be injected and used as following. no string typos, and mismatches
// appConfig.db().host();
```

---

- Correlation Ids to keep track of the request journey
  across different services

```java
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String HEADER_NAME = "X-Correlation-ID";
    private static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String correlationId = request.getHeader(HEADER_NAME);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            MDC.put(MDC_KEY, correlationId);
            response.setHeader(HEADER_NAME, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
```

---

- Custom app error & global exception filter integrated with our logger for re-using logic, keeping things clear &
  relevant,
  and to never expose stack traces to clients or letting the app crash on production

```java
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;

    protected BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
```

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        return buildResponse(ex.getMessage(), ex.getStatus(), null);
    }

    // 1. Catch Validation Errors from JSON Request Bodies (@RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder error = new StringBuilder();

        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String fieldName = ((FieldError) err).getField();
            String errorMessage = err.getDefaultMessage();
            error.append(
                    String.format("%s: %s", fieldName, errorMessage)
            );
        });

        logger.warn("Validation failed for request. Fields broken: {}", error.toString());

        return buildResponse("Validation failed", HttpStatus.BAD_REQUEST, error.toString());
    }
  
    // catchers for all types
}
```

---

- Health endpoints, and signals registration for clear, and easy integration with cloud providers

- Closing connections, and releasing resources on shutdown to prevent leaks, and optimize resource usage

```java
@GetMapping
public Map<String, Object> checkHealth() {
  Map<String, Object> response = new HashMap<>();

  try (Connection conn = dataSource.getConnection();
       Statement stmt = conn.createStatement()) {

    // Execute a simple query to verify connection
    stmt.executeQuery("SELECT 1");

    response.put("status", "UP");
    response.put("database", "CONNECTED");
    log.info("Health check passed");

  } catch (Exception e) {
    log.error("Health check failed: {}", e.getMessage());
    response.put("status", "DOWN");
    response.put("database", "DISCONNECTED");
    response.put("error", e.getMessage());
  }

  return response;
}
```

---

- Storing auth tokens in `httpOnly` cookies to keep sessions secure, with protection against XSS,
  and CSRF

```java
private ResponseCookie createHttpOnlyCookie(String name, String value, long maxAgeSeconds, String path) {
    return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(appConfig.environment() == AppEnvironment.PRODUCTION) // protect from XSS
            .maxAge(maxAgeSeconds)
            .sameSite("Lax")    // protect from CSRF
            .path(path)
            .build();
}
```
---

- Promoting security considerations in code
  - avoiding exposing data in enumeration attacks

```java
@Transactional
public void forgetPassword(ForgetPasswordDto data) {
  // find user or throw
  Optional<UserEntity> result = userRepository.findActiveByEmail(data.getEmail());

  if (result.isEmpty()) {
    // for security consideration, to prevent enumeration attacks
    return;
  }
}
```