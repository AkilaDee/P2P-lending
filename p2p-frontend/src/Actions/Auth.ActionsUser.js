import React, { useState } from 'react';

import axios from 'axios';
import { authConstants } from './Constants';
import { backendUrl } from '../UrlConfig';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export const login = (user) => {
  const notify = () => toast.error(' Signin Error!', {
    position: "top-center",
    autoClose: 5000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
    progress: undefined,
  });
  // console.log("mmmkkk");
  return async (dispatch) => {
    dispatch({ type: authConstants.LOGIN_REQUEST });
    try{
    const res = await axios.post(`${backendUrl}/users/signin`, {
      ...user,
    });

    if (res.status === 200) {
      const { token, userdet } = res.data;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userdet));
      dispatch({
        type: authConstants.LOGIN_SUCCESS,
        payload: {
          token, userdet,
        },
      });
    } else if (res.status === 400) {
      // console.log('one')
     throw new Error("signin error"); 
    }
  }catch(e){
    console.log(e.message)

    notify();
    // dispatch({
    //   type: authConstants.LOGIN_FAILURE,
    //   payload: { error: e.message},
    // });
  }
 
  };
  
};

export const isuserLoggedIn = () => async (dispatch) => {
  const token = localStorage.getItem('token');
  if (token) {
    const user = JSON.parse(localStorage.getItem('user'));
    dispatch({
      type: authConstants.LOGIN_SUCCESS,
      payload: {
        token, user,
      },
    });
  } else {
    dispatch({
      type: authConstants.LOGIN_FAILURE,
      payload: { error: 'Failed to login' },
    });
  }
};

export const signout = () => async (dispatch) => {
  dispatch({ type: authConstants.LOGOUT_REQUEST });
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
};