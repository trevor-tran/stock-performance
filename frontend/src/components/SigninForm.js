import React from 'react';
import { Paper, FormControl, Input, Button } from '@material-ui/core';

import '../css/Form.css'

function SigninForm() {
  return (
    <Paper className="signin-form">
        <h1 className="title">Sign In</h1>
        <p>Enter your username and password below</p>
        <FormControl>
          <label>Username</label>
          <Input className="text-field" placeholder="Enter user name" required />
          <label>Password</label>
          <Input className="text-field" type="password" name="password" placeholder="Enter password" required />
          <Button className="submit" variant="contained" color="primary">Submit</Button>
        </FormControl>
    </Paper>
  );
}

export default SigninForm;
