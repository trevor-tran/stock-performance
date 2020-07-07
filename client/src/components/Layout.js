import React, { useReducer } from "react";
import {urls} from './utils/Constants'
// import ReactDOM from "react-dom";
// import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
// import update from 'immutability-helper';
// import moment from 'moment';

//routers
import { BrowserRouter as Router, Route, Redirect, Switch } from 'react-router-dom';

// other components
import SigninForm from './SigninForm';
import SignupForm from './SignupForm';
import Header from './Header';
import GraphContainer from "./GraphContainer";

// state management
import { Context, initialState, reducer } from '../store'


function Layout() {
  const [state, dispatch] = useReducer(reducer, initialState)

  return (
    <Context.Provider value={{ state, dispatch }}>
      <Router>
        <Header />
        <Switch>
          {/* https://github.com/ReactTraining/react-router/issues/4105#issuecomment-291834881 */}
          {/* an alternative way for component is render={() => <SigninForm/>} */}
          <Route exact path={urls.SIGNIN} component={SigninForm} />
          <Route exact path={urls.SIGNUP} component={SignupForm} />
          <Route exact path={urls.GRAPH} render={() => (
          state.is_authenticated ? (<GraphContainer/>) : (alert("Please log in!"), (<Redirect to={urls.SIGNIN}/>))
          )}/>
          <Route path="*" render={() => (
            <Redirect to= {urls.SIGNIN} />
          )} />
        </Switch>
      </Router>
    </Context.Provider>
  )
}

export default Layout;