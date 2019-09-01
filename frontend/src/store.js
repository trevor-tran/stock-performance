import React from 'react'

export const initialState = {
  budget: 0,
  start_date: "",
  end_date: "",
  symbols: [],
  deleted_symbol: "",
  current_user: ""
}

export function reducer(state, action) {
  switch (action.type) {
    case 'SET_USER':
      return { ...state, current_user: action.payload }
    default:
      return state
  }
}

export const Context = React.createContext(null)