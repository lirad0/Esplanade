// Base URL of the KartaGalaxy backend REST API.
// The backend allows CORS from any origin (see WebConfig.java), so the
// frontend talks to it directly rather than through a same-origin proxy.
// TODO: move this to a proper Angular environment file (environment.ts /
// environment.development.ts) if/when per-environment builds are set up.
export const API_BASE_URL = 'http://localhost:8001/api';
