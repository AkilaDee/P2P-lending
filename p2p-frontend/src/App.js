
import './App.css';
// import {BrowserRouter as Router, Routes} from 'react-router-dom';
import CustomerSignin from './components/customer/SignInCustomer';

// import PrivateRoute from './components/HOC/PrivateRoute';

function App() {
  return (
    <div className="App">
      {/* <Router>
        <Routes>

        <PrivateRoute path="/Customer"  component={CustomerSignin} />
            <Route exact path='/Customer' element={<PrivateRoute/>}>
            <Route exact path='/Customer' element={<CustomerSignin/>}/>
        </Route>

        </Routes>
      </Router>  */}
      <CustomerSignin />
    </div>
  );
}

export default App;
