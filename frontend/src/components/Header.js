import React from 'react';

import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';

import "./css/Header.css";


function Header() {
  return (
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6">Stock Performance</Typography>
          <Button id="signin" variant='outlined' color="inherit">Sign in</Button>
        </Toolbar>
      </AppBar>
  );
}

export default Header;