import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';  // Ensure you are importing Provider
import reportWebVitals from './reportWebVitals';
import App from './App';
import store from './Store';  

window.store = store;// Ensure correct import path

ReactDOM.render(
  <React.StrictMode>
    <Provider store={store}>  
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </Provider>
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals();
