const server = "http://www.phuongtran.me/stock";
// const server = "http://localhost:3000/stock"
export const fetchURLs = {
  SIGNIN: server + "/server/signin/",
  SIGNUP: server + "/server/signup/",
  DATA: server + "/server/data/",
}

export const clientURLs = {
  SIGNIN: "/stock/signin/",
  SIGNUP: "/stock/signup/",
  GRAPH: "/stock/graph/"
}

export const keys = {
  ENTER: 13
}

export const types = {
  LOGIN : 'LOGIN',
  LOGOUT : 'LOGOUT',
  ADD_SYMBOL: 'ADD_SYMBOL',
  REMOVE_SYSMBOL: 'REMOVE_SYMBOL',
  START_DATE: 'START_DATE',
  END_DATE: 'END_DATE',
  BUDGET: 'BUDGET',
  ALL: 'ALL'
}

export const sessions = {
  USER: "user",
  AUTHENTICATED: "is_authenticated",
  USER_STATE: "user_state",
  STOCK_DATA: "stock_data"
}