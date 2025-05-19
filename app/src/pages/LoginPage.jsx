// app/src/pages/LoginPage.jsx
import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { exchangeToken } from '../api/auth';

export default function LoginPage() {
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const { search } = useLocation();

  useEffect(() => {
    async function handleAuth() {
      const params = new URLSearchParams(search);
      if (!params.has('code')) {
        setError('Authorization code missing');
        return;
      }

      try {
        const code = params.get('code');
        const verifier = localStorage.getItem('pkce_verifier');
        const { access_token, id_token } = await exchangeToken(code, verifier);
        
        localStorage.setItem('access_token', access_token);
        localStorage.setItem('id_token', id_token);
        navigate('/');
      } catch (err) {
        console.error('Authentication failed:', err);
        setError(err.message);
      }
    }

    handleAuth();
  }, [search, navigate]);

  if (error) return <p className="text-red-500">Error: {error}</p>;
  return <p>Authenticating...</p>;
}