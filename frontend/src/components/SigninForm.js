import React, { useState } from 'react';
import { Paper, FormControl, Input, Button } from '@material-ui/core';

import './css/Form.css'

function SigninForm() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  return (
    <Paper className="signin-form">
        <h1 className="title">Sign In</h1>
        <label>Enter your username and password below</label>
        <FormControl>
          <label>Username</label>
          <Input 
            className="text-field" 
            placeholder="Enter user name" 
            required
            onChange={e => setUsername(e.target.value)}
          />
          <label>Password</label>
          <Input 
            className="text-field" 
            type="password" 
            placeholder="Enter password" 
            required
            onChange={e => setPassword(e.target.value)} 
          />
          <Button 
            className="submit" 
            variant="contained" 
            color="primary"
            onClick={()=> {
              console.log( username)
              console.log (password)
            }}>
            Submit
          </Button>
        </FormControl>
    </Paper>
  );
}

export default SigninForm;
