// actions/auth.actionuser.js
import axios from 'axios';
import { authConstants } from './Constants';
import { backendUrl } from '../UrlConfig';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const notify = (message) => toast.error(message, {
  position: "top-center",
  autoClose: 5000,
  hideProgressBar: false,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  progress: undefined,
});

export const login = (user) => {
  return async (dispatch) => {
    dispatch({ type: authConstants.LOGIN_REQUEST });
    try {
      const res = await axios.post(`${backendUrl}/users/signin`, user);

      if (res.status === 200) {
        const { token, user, role } = res.data;
        localStorage.setItem('user', JSON.stringify(user));
        localStorage.setItem('role', role); // Store the role
        localStorage.setItem('isAuthenticated', 'true');
        dispatch({
          type: authConstants.LOGIN_SUCCESS,
          payload: { token, user, role },
        });
      } else {
        notify("Signin Error!");
        dispatch({
          type: authConstants.LOGIN_FAILURE,
          payload: { error: "Signin Error!" },
        });
      }
    } catch (error) {
      notify("Signin Error!");
      dispatch({
        type: authConstants.LOGIN_FAILURE,
        payload: { error: error.message },
      });
    }
  };
};

export const isuserLoggedIn = () => (dispatch) => {
  const token = localStorage.getItem('token');
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';
  if (token && isAuthenticated) {
    try {
      const user = JSON.parse(localStorage.getItem('user'));
      const role = localStorage.getItem('role'); // Retrieve role
      if (user) {
        dispatch({
          type: authConstants.LOGIN_SUCCESS,
          payload: { token, user, role },
        });
      } else {
        throw new Error("User data is invalid");
      }
    } catch (error) {
      dispatch({
        type: authConstants.LOGIN_FAILURE,
        payload: { error: 'Failed to login' },
      });
    }
  } else {
    dispatch({
      type: authConstants.LOGIN_FAILURE,
      payload: { error: 'Failed to login' },
    });
  }
};

export const signout = () => async (dispatch) => {
  dispatch({ type: authConstants.LOGOUT_REQUEST });
  try {
    const res = await axios.post(`${backendUrl}/signout`);

    if (res.status === 200) {
      localStorage.clear();
      dispatch({ type: authConstants.LOGOUT_SUCCESS });
    } else {
      dispatch({
        type: authConstants.LOGOUT_FAILURE,
        payload: { error: res.data.error },
      });
    }
  } catch (error) {
    dispatch({
      type: authConstants.LOGOUT_FAILURE,
      payload: { error: error.message },
    });
  }
};
