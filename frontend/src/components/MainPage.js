import React, { useState } from "react";
import ReactDOM from "react-dom";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import update from 'immutability-helper';
import moment from 'moment';

//routers
import { BrowserRouter as Router, Route, Redirect, Switch} from 'react-router-dom';

// other components
import SigninForm from './SigninForm';
import SignupForm from './SignupForm';
import Header from './Header';


function MainPage() {
  const [budget, setBudget] = useState("");
  const [start_date, setStartDate] = useState("");
  const [end_date, setEndDate] = useState("");
  const [symbols, setSymbols] = useState([]);
  const [deleted_symbol, setDeletedSymbol] = useState("");
  return (
    <React.Fragment>
      <Router>
        <Header />
        <Switch>
          <Route exact path="/signin/" component={SigninForm} />
          <Route exact path="/signup/" component={SignupForm} />
          <Route path="*" render={() => (
            <Redirect to="/signin/" />
          )} />
        </Switch>
      </Router>
    </React.Fragment>
  )
}

export default MainPage;