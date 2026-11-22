# Esplanade Backend

This module provides a Spring Boot backend for the Angular App.

## API examples

### Username/password authentication
Set `JWT_SECRET` to a Base64-encoded random secret of at least 32 bytes before starting
the backend or running Docker Compose. For example, generate one with
`openssl rand -base64 32`. The optional `JWT_EXPIRATION_TIME` setting is in
milliseconds and defaults to 86400000 (24 hours).

Sign up (username must be 3-50 characters; password must be at least 8 characters):
```bash
curl -X POST http://localhost:8001/api/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"correct horse battery staple"}'
```

Authenticate:
```bash
curl -X POST http://localhost:8001/api/auth/authenticate \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"correct horse battery staple"}'
```

Both endpoints return an `accessToken`, `tokenType`, `expiresIn` (seconds), and
`username`. Send the token as `Authorization: Bearer <accessToken>` to access the
other `/api/**` endpoints. `/api/health` and `/file/**` remain public.

### Health
```bash
curl http://localhost:8001/api/health
```
