import axios from 'axios';

const AUTH_SERVER = 'http://localhost:9000';

export async function getAuthUrl() {
  const params = new URLSearchParams({
    response_type: 'code',
    client_id: 'spa-client',
    redirect_uri: 'http://localhost:3000/',
    scope: 'openid profile email',
    code_challenge_method: 'S256',
    code_challenge: localStorage.getItem('pkce_challenge'),
  });
  return `${AUTH_SERVER}/oauth2/authorize?${params.toString()}`;
}

export async function exchangeToken(code, codeVerifier) {
  const params = new URLSearchParams({
    grant_type: 'authorization_code',
    code,
    redirect_uri: 'http://localhost:3000/',
    client_id: 'spa-client',
    code_verifier: codeVerifier,
  });
  const resp = await axios.post(`${AUTH_SERVER}/oauth2/token`, params);
  return resp.data; // { access_token, refresh_token, id_token, ... }
}
