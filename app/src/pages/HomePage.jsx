// app/src/pages/HomePage.jsx
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function HomePage() {
  const [userInfo, setUserInfo] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      navigate('/login');
      return;
    }
    
    // Parse JWT token payload
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      setUserInfo(payload);
    } catch (error) {
      console.error('Invalid token:', error);
      localStorage.removeItem('access_token');
      navigate('/login');
    }
  }, [navigate]);

  if (!userInfo) return <p>Loading...</p>;

  return (
    <div className="p-8">
      <h2 className="text-xl font-semibold">Welcome, {userInfo.preferred_username}</h2>
      <pre className="bg-gray-100 p-4 rounded mt-4">
        {JSON.stringify(userInfo, null, 2)}
      </pre>
    </div>
  );
}