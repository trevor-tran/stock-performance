import React from 'react';
import {Link, Redirect, BrowserRouter as Router, Route, Switch} from "react-router-dom";
import { Button, Typography, Toolbar, AppBar, FormControl} from '@material-ui/core';
import SigninForm from './SigninForm';
import SignupForm from './SignupForm';
import "./css/Header.css";
import { withRouter } from 'react-router-dom'


function Header() {

  return (
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6">Stock Performance</Typography>
          <FormControl id="menu">
      <Router>

            <Route render = {() => (
              <Button className="button" variant='outlined' color="inherit" onClick={() => <Redirect to='/signup/'/>}>Sign up</Button>
            )}/>
            {/* <Button className="button" variant='outlined' color="inherit">Sign in</Button> */}
      </Router>

          </FormControl>
          {/* <Switch>
            <Route  exact path="/signin/" component={SigninForm} />
          <Route path="/signup/" component={SignupForm} />
          </Switch> */}
        </Toolbar>
      </AppBar>

  );
}

export default Header;