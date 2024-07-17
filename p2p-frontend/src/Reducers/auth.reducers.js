// reducers/auth.reducers.js
import { authConstants } from "../Actions/Constants";

const initState = {
  token: null,
  user: {
    email: "",
  },
  role: null, // Add role field
  authenticate: false,
  authenticating: false,
  loading: false,
  error: null,
  message: "",
};

export default (state = initState, action) => {
  switch (action.type) {
    case authConstants.LOGIN_REQUEST:
      return {
        ...state,
        authenticating: true,
      };
    case authConstants.LOGIN_SUCCESS:
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        role: action.payload.role, // Update role
        authenticate: true,
        authenticating: false,
      };
    case authConstants.LOGOUT_REQUEST:
      return {
        ...initState,
        loading: true,
      };
    case authConstants.LOGOUT_SUCCESS:
      return {
        ...initState,
      };
    case authConstants.LOGOUT_FAILURE:
      return {
        ...state,
        error: action.payload.error,
        loading: false,
      };
    default:
      return state;
  }
};
