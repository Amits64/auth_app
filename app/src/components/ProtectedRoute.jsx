// app/src/components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const isAuthenticated = !!localStorage.getItem('access_token');
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

export default ProtectedRoute;