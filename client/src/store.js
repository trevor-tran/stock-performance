import React from 'react'
import {types} from './components/utils/Constants'

export const initialState = {
  budget: 1000,
  start_date: "",
  end_date: "",
  symbols: [],
  user: "",
  is_authenticated: false
}

export function reducer(state = initialState, action) {
  switch (action.type) {
    case types.ALL:
      const newState = action.payload;
      return newState;
    case types.LOGIN:
      return { ...state,
        user: action.payload.user,
        first_name: action.payload.first_name,
        token: action.payload.token,
        is_authenticated: true
      }
    case types.LOGOUT:
      sessionStorage.clear();
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
    case types.START_DATE:
      return {...state,
        start_date: action.payload.startDate
      }
    case types.END_DATE:
      return {...state,
        end_date: action.payload.endDate
      }
    case types.BUDGET:
      return {
        ...state,
        budget: action.payload.budget
      }
    default:
      return state
  }
}

export const Context = React.createContext(null)