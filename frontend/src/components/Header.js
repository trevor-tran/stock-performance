import React from 'react';

import { Divider, Button, Typography, Toolbar, AppBar, FormControl} from '@material-ui/core';

import "./css/Header.css";


function Header() {
  return (
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6">Stock Performance</Typography>
          <FormControl id="menu">
            <Button className="button" variant='outlined' color="inherit">Sign up</Button>
            <Button className="button"  variant='outlined' color="inherit">Sign in</Button>
          </FormControl>
        </Toolbar>
      </AppBar>
  );
}

export default Header;