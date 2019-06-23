import React from 'react';
import {Link, Redirect, BrowserRouter as Router, Route} from "react-router-dom";
import { Button, Typography, Toolbar, AppBar, FormControl} from '@material-ui/core';
import SigninForm from './SigninForm';
import SignupForm from './SignupForm';
import "./css/Header.css";


function Header() {

  return (
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6">Stock Performance</Typography>
          <FormControl id="menu">
          <Router>

            {/* <Button className="button" component={Link} to="/signup/" variant='outlined' color="inherit">Sign up</Button> */}
            <Button className="button" component={Link} to="/signin/" variant='outlined' color="inherit">Sign in</Button>
            <Route path="/signin/" component={SigninForm} />
          {/* <Route path="/signup/" component={SignupForm} /> */}
          </Router>
          </FormControl>
        </Toolbar>
      </AppBar>
  );
}

export default Header;