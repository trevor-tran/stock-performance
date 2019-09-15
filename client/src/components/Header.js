import React,{useContext} from 'react';
import { Link } from "react-router-dom";
import { Button, Typography, Toolbar, AppBar, FormControl } from '@material-ui/core';
import {withRouter} from 'react-router-dom'
import {types,urls} from './utils/Constants'
import { Context } from '../store';

import "./css/Header.css";


const Header = withRouter(({history}) => {
  const {state, dispatch} = useContext(Context)
  const signout = () => {
    dispatch({type: types.LOGOUT})
    history.push(urls.SIGNIN)
  }

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6">Stock Performance</Typography>
        <FormControl id="menu">
          {state.is_authenticated ? 
            (<Button className="button" color="inherit" onClick={signout}>Sign out</Button>) :
            (<Button component={Link} to={urls.SIGNIN} className="button" color="inherit"> Sign in </Button>)
          }
          <Button component={Link} to={urls.SIGNUP} className="button" variant='outlined' color="inherit">Sign up</Button>
        </FormControl>
      </Toolbar>
    </AppBar>

  );
})

export default Header;