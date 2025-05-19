export async function generatePKCE() {
  const array = new Uint8Array(32);
  crypto.getRandomValues(array);
  const verifier = btoa(String.fromCharCode(...array)).slice(0,128);
  const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(verifier));
  const challenge = btoa(String.fromCharCode(...new Uint8Array(digest)))
    .replace(/\+/g,'-').replace(/\//g,'_').replace(/=+$/,'');
  localStorage.setItem('pkce_verifier', verifier);
  localStorage.setItem('pkce_challenge', challenge);
}
