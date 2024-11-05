import { configureStore, createSlice } from "@reduxjs/toolkit";

let accessToken = createSlice({
  name: 'accessToken',
  initialState: {
    JWT: '',
  },
  reducers: {
    changeToken(state, action) {
      state.JWT = action.payload;
      console.log("state.JWT: " + state.JWT);
    },
  },
});

export let { changeToken } = accessToken.actions;

export default configureStore({
  reducer: {
    accessToken: accessToken.reducer,
  },
});