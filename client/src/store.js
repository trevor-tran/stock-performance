import React from 'react'
import {types} from './components/utils/Constants'

export const initialState = {
  budget: 0,
  start_date: "",
  end_date: "",
  symbols: [],
  deleted_symbol: "",
  current_user: "",
  first_name:""
}

export function reducer(state, action) {
  switch (action.type) {
    case types.SET_USER:
      return { ...state, current_user: action.payload }
    case types.SET_FIRST_NAME:
      return {...state, first_name: action.payload}
    default:
      return state
  }
}

export const Context = React.createContext(null)