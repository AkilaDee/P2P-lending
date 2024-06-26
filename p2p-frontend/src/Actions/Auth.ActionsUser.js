import axios from 'axios';
import { authConstants } from './Constants';
import { backendUrl } from '../UrlConfig';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Function to display error notifications
const notify = (message) => toast.error(message, {
  position: "top-center",
  autoClose: 5000,
  hideProgressBar: false,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  progress: undefined,
});

// Action for user login
export const login = (user) => {
  return async (dispatch) => {
    dispatch({ type: authConstants.LOGIN_REQUEST });
    try {
      const res = await axios.post(`${backendUrl}/users/signin`, user);
      console.log('Server response:', res); // Log the response for debugging

      if (res.status === 200) {
        const token = 'abcd';
        const userdet = res.data;
        if (userdet) {
          localStorage.setItem('user', JSON.stringify(userdet));
          localStorage.setItem('isAuthenticated', 'true'); // Set isAuthenticated flag
          dispatch({
            type: authConstants.LOGIN_SUCCESS,
            payload: { token, userdet },
          });
        } else {
          throw new Error("Invalid response structure: missing token or userdet");
        }
      } else {
        notify("Signin Error!");
        dispatch({
          type: authConstants.LOGIN_FAILURE,
          payload: { error: "Signin Error!" },
        });
      }
    } catch (error) {
      console.error('Error during signin:', error.message);
      notify("Signin Error!");
      dispatch({
        type: authConstants.LOGIN_FAILURE,
        payload: { error: error.message },
      });
    }
  };
};

// Action to check if user is logged in
export const isuserLoggedIn = () => (dispatch) => {
  const token = localStorage.getItem('token');
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';
  if (token && isAuthenticated) {
    try {
      const user = JSON.parse(localStorage.getItem('user'));
      if (user) {
        dispatch({
          type: authConstants.LOGIN_SUCCESS,
          payload: { token, user },
        });
      } else {
        throw new Error("User data is invalid");
      }
    } catch (error) {
      console.error('Error parsing user data:', error.message);
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

// Action to sign out user
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
    console.error('Error during signout:', error.message);
    dispatch({
      type: authConstants.LOGOUT_FAILURE,
      payload: { error: error.message },
    });
  }
};
