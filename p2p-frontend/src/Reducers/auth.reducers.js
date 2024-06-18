import { authConstants } from "../Actions/Constants";

const initState = {
  token: null,
  user: {
    email: "",
  },
  authenticate: false,
  authenticating: false,
  loading: false,
  error: null,
  message: "",
};

export default (state = initState, action) => {
  console.log(action);
  switch (action.type) {
    case authConstants.LOGIN_REQUEST:
      state = {
        ...state,
        authenticating: true,
      };
      break;
    case authConstants.LOGIN_SUCCESS:
      state = {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        authenticate: true,
        authenticating: false,
      };
      break;
    case authConstants.LOGOUT_REQUEST:
      state = {
        ...initState,  // Corrected from ...initstate
        loading: true,
      };
      break;
    case authConstants.LOGOUT_SUCCESS:
      state = {
        ...initState,  // Corrected from ...initstate
      };
      break;
    case authConstants.LOGOUT_FAILURE:
      state = {
        ...state,
        error: action.payload.error,
        loading: false,
      };
      break;
    default:
      break;
  }

  return state;
};
