import React, { useState, useContext } from 'react';
import { Paper, FormControl, Input, Button } from '@material-ui/core';
import { Context } from '../store';
import { withRouter } from 'react-router-dom'

// css
import './css/Form.css'

const SignupForm = withRouter(({history}) => {
  
  const {state, dispatch} = useContext(Context)


  const [first_name, setFirstName] = useState("");
  const [last_name, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirm_password, setConfirmPassword] = useState("");
  const [message, setMessage] = useState(undefined)

  const submit = () => {
    if (state.current_user) {
      // a user already logged in
      setMessage("Please log out before sign up")
    } else if (password !== confirm_password) {
      setMessage("Password does not match")
    }else {
      // validate info on server (check if username already exists etc.)
      let url = "http://localhost:4567/signup/";
      fetch(url, {
        method: 'POST',
        body: JSON.stringify({
          'firstname': first_name,
          'lastname': last_name,
          'emailaddress': email,
          'username': username,
          'password': password
        }),
        headers:{
          'Content-Type': 'application/json'
        }
      }).then(response => {
        return response.json()
      }).then (json => {
        // status is either "failure" or "success"
        if (json.status === "failure") {
          setMessage(json.msg)         
        }else {
          dispatch({type:'SET_USER', payload: username});
          history.push('/graph/')
        }
      }).catch(err => {
        console.error(err);
      });
    }
  }

  return (
    <Paper className="signin-form">
      <h1 className="title">Sign-up Form</h1>
      { message ? (<p style={{color:"red"}}>{message}</p>) : null}
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
        <label>Username</label>
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
          onChange={e => setConfirmPassword(e.target.value)}
        />
        <Button
          className="submit"
          variant="contained"
          color="primary"
          onClick={submit}>
          Submit
          </Button>
      </FormControl>
    </Paper>
  );
})

export default SignupForm
