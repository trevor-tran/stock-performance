import React from 'react';
import { BrowserRouter as Router, Route, Redirect } from 'react-router-dom';
import logo from './logo.svg';
import './App.css';
import Main from './components/Main';
import SigninForm from './components/SigninForm';


function App() {
  return (
    <Router>
       <Route path="/signin" component={SigninForm} />
      <Route path="*" render={() => (
        <Redirect to="/signin" />
      )} />
    </Router>
  );
}

export default App;
