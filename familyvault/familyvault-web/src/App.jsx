import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Families from './pages/Families';
import FamilyDetail from './pages/FamilyDetail';

function PrivateRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
  }

  return isAuthenticated ? children : <Navigate to="/login" />;
}

function PublicRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
  }

  return !isAuthenticated ? children : <Navigate to="/families" />;
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route
            path="/login"
            element={
              <PublicRoute>
                <Login />
              </PublicRoute>
            }
          />
          <Route
            path="/register"
            element={
              <PublicRoute>
                <Register />
              </PublicRoute>
            }
          />
          <Route
            path="/families"
            element={
              <PrivateRoute>
                <Families />
              </PrivateRoute>
            }
          />
          <Route
            path="/families/:familyId"
            element={
              <PrivateRoute>
                <FamilyDetail />
              </PrivateRoute>
            }
          />
          <Route path="/" element={<Navigate to="/families" />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
