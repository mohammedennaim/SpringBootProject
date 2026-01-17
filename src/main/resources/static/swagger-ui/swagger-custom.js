// Swagger UI custom logout helper
// Attempts to find the ID token (id_token) stored by the OAuth flow, clears Swagger UI auth,
// then redirects to the backend Keycloak logout endpoint with idTokenHint and post logout redirect.
(function(){
  'use strict';

  function tryParseJson(value){
    try { return JSON.parse(value); } catch(e){ return null; }
  }

  function deepFind(obj, keyCandidates) {
    if (!obj || typeof obj !== 'object') return null;
    try {
      for (const k of Object.keys(obj)){
        if (keyCandidates.includes(k)) return obj[k];
        const v = obj[k];
        if (typeof v === 'object'){
          const found = deepFind(v, keyCandidates);
          if (found) return found;
        }
      }
    } catch(e) {}
    return null;
  }

  function findIdTokenInStorage(storage){
    try {
      for (let i=0;i<storage.length;i++){
        const key = storage.key(i);
        const raw = storage.getItem(key);
        // try parse
        const parsed = tryParseJson(raw);
        if (parsed){
          // common names
          const found = deepFind(parsed, ['id_token','idToken','idTokenHint','id_token_hint']);
          if (typeof found === 'string' && found.length>0) return found;
        } else if (typeof raw === 'string'){
          // quick heuristic: JWT has two dots
          if ((raw.match(/\./g)||[]).length===2 && raw.split('.').length===3) return raw;
        }
      }
    } catch(e){}
    return null;
  }

  function findIdToken(){
    // Check sessionStorage, localStorage
    let t = findIdTokenInStorage(window.sessionStorage || {});
    if (t) return t;
    t = findIdTokenInStorage(window.localStorage || {});
    if (t) return t;

    // Try to inspect swagger-ui internal token holder if present
    try {
      if (window.ui && window.ui.authSelectors && typeof window.ui.authSelectors.getAuth === 'function'){
        const auth = window.ui.authSelectors.getAuth(window.ui.getSystem());
        const found = deepFind(auth, ['id_token','idToken','id_token_hint']);
        if (found) return found;
      }
    } catch(e){}

    return null;
  }

  function clearSwaggerAuth(){
    try {
      if (window.ui && window.ui.authActions && typeof window.ui.authActions.logout === 'function'){
        window.ui.authActions.logout();
      }
    } catch(e){ console.warn('clear swagger auth failed', e); }
  }

  function logoutFlow(){
    const idToken = findIdToken();
    clearSwaggerAuth();

    const redirect = window.location.origin + window.location.pathname;
    let url = '/api/auth/logout/keycloak?redirectUri=' + encodeURIComponent(redirect);
    if (idToken) {
      url += '&idTokenHint=' + encodeURIComponent(idToken);
    }

    // full redirect to Keycloak logout via backend
    window.location.href = url;
  }

  function addButton(){
    if (document.getElementById('swagger-keycloak-logout')) return;
    const btn = document.createElement('button');
    btn.id = 'swagger-keycloak-logout';
    btn.type = 'button';
    btn.innerText = 'Logout Keycloak';
    btn.title = 'Clear Swagger auth and logout from Keycloak (uses id_token if available)';
    btn.style = 'position:fixed;right:12px;top:12px;z-index:9999;padding:8px 12px;background:#d9534f;color:#fff;border:none;border-radius:4px;cursor:pointer;font-weight:600;box-shadow:0 1px 2px rgba(0,0,0,0.2)';
    btn.addEventListener('click', function(e){ e.preventDefault(); logoutFlow(); });
    document.body.appendChild(btn);
  }

  if (document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', addButton);
  } else {
    addButton();
  }

})();
