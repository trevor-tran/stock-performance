import React from 'react'
import {types} from './components/utils/Constants'

export const initialState = {
  budget: 0,
  start_date: null,
  end_date: null,
  symbols: [],
  deleted_symbol: null,
  user: null,
  token: null,
  first_name: null,
  is_authenticated: false
}

export function reducer(state = initialState, action) {
  switch (action.type) {
    case types.LOGIN:
      localStorage.setItem("user", JSON.stringify(action.payload.user))
      localStorage.setItem("token", JSON.stringify(action.payload.token))
      return { ...state, 
        user: action.payload.user, 
        first_name: action.payload.first_name,
        token: action.payload.token,
        is_authenticated: true
      }
    case types.LOGOUT:
      localStorage.clear();
      return {...initialState}
    default:
      return state
  }
}

export const Context = React.createContext(null)