import React from 'react';
import { Routes, Route, Router } from 'react-router-dom';
import Signin from './containers/Users/SignInUser';
import MainLandingPage from './containers/Users/MainLandingPage';
import SignUp from './containers/Users/Signup';
import PrivateRoute from './components/HOC/PrivateRoute';
import User from './containers/Users/Layout/User';
import LendRequests from './containers/Users/LendRequests';
import Dashboard from './containers/Users/Dashboard';
import LoanRequests from './containers/Users/LoanRequests';
// import Routes from './containers/Users/Routes/';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<MainLandingPage />} />
        <Route path="/login" element={<Signin />} />
        <Route path="/register" element={<SignUp />} />
        <Route path="/user/*" element={<PrivateRoute />}>
          <Route path="*" element={<User />} />
        </Route>
{/* *       <Route element={<User />} path="/user" >
          <Route element={<Dashboard/>} path="/user/dashboard" />
          <Route element={<LoanRequests/>} path="/user/loanrequests" />
          <Route element={<LendRequests/>} path="/user/lendrequests" />
          
           

        </Route>
        */}
      </Routes>
    </div>
  );
}

export default App;
