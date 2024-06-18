import './App.css';
import { Routes, Route } from 'react-router-dom'; // Remove BrowserRouter alias
import Signin from './containers/Users/SignInCustomer';
import MainLandingPage from './containers/Users/MainLandingPage';
import SignUp from './containers/Users/Signup';
import PrivateRoute from './components/HOC/PrivateRoute';
import Dashboard from './containers/Users/Dashboard';
import LoanRequests from './containers/Users/LoanRequests';
import LendRequests from './containers/Users/LendRequests';
import User from './containers/Users/Layout/Users.js';

// Mock authentication status for demonstration
const isAuthenticated = true; // Replace with your actual authentication logic

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<MainLandingPage />} />
        <Route path="/login" element={<Signin />} />
        <Route path="/register" element={<SignUp />} />

        {/* Wrap protected routes with PrivateRoute */}
        <Route element={<PrivateRoute />}>
          <Route path="/user" element={<User />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/loanrequests" element={<LoanRequests />} />
          <Route path="/lendrequests" element={<LendRequests />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
