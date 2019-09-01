import React, { useReducer } from "react";
import ReactDOM from "react-dom";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import update from 'immutability-helper';
import moment from 'moment';

//routers
import { BrowserRouter as Router, Route, Redirect, Switch } from 'react-router-dom';

// other components
import SigninForm from './SigninForm';
import SignupForm from './SignupForm';
import Header from './Header';
import Graph from "./Graph";

// state management
import { Context, initialState, reducer } from '../store'


function MainPage() {
  const [state, dispatch] = useReducer(reducer, initialState)

  return (
    <Context.Provider value={{ state, dispatch }}>
      <Router>
        <Header />
        <Switch>
          {/* https://github.com/ReactTraining/react-router/issues/4105#issuecomment-291834881 */}
          {/* an alternative way for component is render={() => <SigninForm/>} */}
          <Route exact path="/signin/" component={SigninForm} />
          <Route exact path="/signup/" component={SignupForm} />
          <Route exact path="/graph/" render={() => (
          state.current_user ? (<Graph/>) : (alert("Please log in!"), (<Redirect to="/signin/"/>)) 
          )}/>
          <Route path="*" render={() => (
            <Redirect to="/signin/" />
          )} />
        </Switch>
      </Router>
    </Context.Provider>
  )
}

export default MainPage;