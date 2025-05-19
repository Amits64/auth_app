// app/src/pages/ErrorPage.jsx
import { useNavigate } from 'react-router-dom';

export default function ErrorPage() {
  const navigate = useNavigate();
  
  return (
    <div className="p-8 text-center">
      <h1 className="text-2xl font-bold text-red-600">404 - Page Not Found</h1>
      <button 
        onClick={() => navigate('/')}
        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
      >
        Return to Home
      </button>
    </div>
  );
}