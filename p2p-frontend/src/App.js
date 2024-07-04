import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Signin from './containers/Users/SignInUser';
import MainLandingPage from './containers/Users/MainLandingPage';
import SignUp from './containers/Users/Signup';
import PrivateRoute from './components/HOC/PrivateRoute';
import User from './containers/Users/Layout/User';

function App() {
  return (
    <div className="App">
      <Router>
        <Switch>
          <Route exact path="/" component={MainLandingPage} />
          <Route path="/login" component={Signin} />
          <Route path="/register" component={SignUp} />
          <PrivateRoute path="/user" component={User} />
        </Switch>
      </Router>
    </div>
  );
}

export default App;
