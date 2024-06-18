import { createStore, applyMiddleware } from 'redux';
import { thunk } from 'redux-thunk';  // Change this line
import rootReducer from '../Reducers';  // Ensure the path is correct

const store = createStore(rootReducer, applyMiddleware(thunk));

export default store;
