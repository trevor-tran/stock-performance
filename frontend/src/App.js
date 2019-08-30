import React from 'react';
import { BrowserRouter as Router, Route, Redirect, Switch} from 'react-router-dom';

// other components
import SigninForm from './components/SigninForm';
import SignupForm from './components/SignupForm';
import Header from './components/Header';

//css
import './App.css';

function App() {
  return (
    <React.Fragment>
      <Router>
      <Header/>
        <Switch>
        <Route exact path="/signin/" component={SigninForm} />
        <Route exact path="/signup/" component={SignupForm} />
        <Route path="*" render={() => (
          <Redirect to="/signin/" />
        )} />
        </Switch>
      </Router>
    </React.Fragment>
  );
}

export default App;
