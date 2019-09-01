import React,{useContext} from 'react';
import { Link } from "react-router-dom";
import { Button, Typography, Toolbar, AppBar, FormControl } from '@material-ui/core';
import {withRouter} from 'react-router-dom'

import { Context } from '../store';

import "./css/Header.css";


const Header = withRouter(({history}) => {
  const {state, dispatch} = useContext(Context)
  const signout = () => {
    dispatch({type:"SET_USER", payload: ""})
    history.push('/signin/')
  }

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6">Stock Performance</Typography>
        <FormControl id="menu">
          {/* current_user indicates login/logout status. 
          It stores current username if a user logged in. Otherwise, it's falsy  */}
          {!state.current_user ? 
            (<Button component={Link} to='/signin/' className="button" color="inherit"> Sign in </Button>) : 
            (<Button className="button" color="inherit" onClick={signout}>Sign out</Button>)
          }
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
})

export default Header;