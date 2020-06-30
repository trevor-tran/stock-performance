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
    case types.ADD_SYMBOL:
      const foundIndex = state.symbols.findIndex(e => e === action.payload.symbol);
      if (foundIndex >= 0) {
        // not adding a new symbol if already exists
        return state;
      }
      const updatedSymbols = [...state.symbols, action.payload.symbol];
      return {...state,
        symbols: updatedSymbols
      }
    case types.REMOVE_SYSMBOL:
      const removedSymbols = state.symbols.filter(s => s !== action.payload.symbol);
      return {...state,
        symbols: removedSymbols
      }
    default:
      return state
  }
}

export const Context = React.createContext(null)