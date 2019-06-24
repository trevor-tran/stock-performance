import React, { useState } from 'react';
import { Paper, FormControl, Input, Button } from '@material-ui/core';

import './css/Form.css'

function SignupForm() {
  const [first_name, setFirstName] = useState("");
  const [last_name, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  return (
    <Paper className="signin-form">
        <h1 className="title">Sign-up Form</h1>
        <FormControl>
          <label>First name</label>
          <Input 
            className="text-field" 
            placeholder="Enter first name" 
            required
            onChange={e => setFirstName(e.target.value)}
          />
          <label>Last name</label>
          <Input 
            className="text-field" 
            placeholder="Enter last name"
            required
            onChange={e => setLastName(e.target.value)} 
          />
          <label>Email address</label>
          <Input 
            className="text-field" 
            type="email" 
            placeholder="Enter email address"
            required
            onChange={e => setEmail(e.target.value)} 
          />
          <label>New username</label>
          <Input 
            className="text-field" 
            placeholder="Enter new username"
            required
            onChange={e => setUsername(e.target.value)} 
          />
          <label>Enter password</label>
          <Input 
            className="text-field" 
            type="password"
            placeholder="Enter password"
            required
            onChange={e => setPassword(e.target.value)} 
          />
          <label>Re-enter password</label>
          <Input 
            className="text-field" 
            type="password"
            placeholder="Re-enter password"
            required
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

export default SignupForm;
