import React, { useReducer} from "react";
import {clientURLs, sessions} from './utils/Constants'

//routers
import { BrowserRouter as Router, Route, Redirect, Switch } from 'react-router-dom';

// other components
import SigninForm from './SigninForm';
import SignupForm from './SignupForm';
import Header from './Header';
import Footer from './Footer';
import GraphContainer from "./GraphContainer";
import NotFound from './NotFound'
// state management
import { Context, initialState, reducer } from '../store'


function Layout() {
  let newState = initialState;
  if (sessionStorage.getItem(sessions.USER_STATE)) {
    newState = JSON.parse(sessionStorage.getItem(sessions.USER_STATE));
  }

  const [state, dispatch] = useReducer(reducer, newState);

  return (
    <Context.Provider value={{ state, dispatch }}>
      <Router>
        <Header />
        <Switch>
          {/* https://github.com/ReactTraining/react-router/issues/4105#issuecomment-291834881 */}
          {/* an alternative way for component is render={() => <SigninForm/>} */}
          {/* <Route exact path={urls.SIGNIN} component={SigninForm} /> */}

          <Route exact path={clientURLs.SIGNIN} render={() => (
            state.is_authenticated ? <Redirect to={clientURLs.GRAPH}/> : <SigninForm/>
          )}/>
          <Route exact path={clientURLs.SIGNUP} render={() => (
            state.is_authenticated ? <Redirect to={clientURLs.GRAPH}/> : <SignupForm/>
          )}/>
          <Route exact path={clientURLs.GRAPH} render={() => (
          state.is_authenticated ? <GraphContainer/> : <Redirect to={clientURLs.SIGNIN}/>
          )}/>
           <Route path="/stock/" render={() => {
            return <Redirect to={clientURLs.SIGNIN}/>
          }} />
          <Route path="*" render={() => {
            return <NotFound/>
          }} />
        </Switch>
        <Footer/>
      </Router>
    </Context.Provider>
  )
}

export default Layout;