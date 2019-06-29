import React from 'react';
import { Link, Redirect, BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { Button, Typography, Toolbar, AppBar, FormControl } from '@material-ui/core';
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
          <Button 
            component={Link} 
            to='/signin/' 
            className="button" 
            color="inherit"
          > 
            Sign in
          </Button>
          <Button 
            component={Link} 
            to='/signup/' 
            className="button" 
            variant='outlined' 
            color="inherit"
          >
            Sign up
          </Button>
        </FormControl>
      </Toolbar>
    </AppBar>

  );
}

export default Header;