import React, { useState, useContext } from 'react';
import { Paper, FormControl, Input, Button } from '@material-ui/core';
import {withRouter} from 'react-router-dom'
import {Context} from '../store'

// constants
import {types, urls, keys} from './utils/Constants'
// css
import './css/Form.css'

const SigninForm = withRouter(({history}) => {
  const {dispatch} = useContext(Context)
  // local states
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const submit = () => {
    let url = urls.SERVER_URL + urls.SIGNIN;
    fetch(url, {
      method: 'POST',
      body: JSON.stringify({
        'username': username,
        'password': password
      })
    }).then (response => {
      return response.json();
    }).then (json => {
      // status is either "failure" or "success"
      if (json.status === "failure") {
        // fail!!! server sends back error message
        setMessage(json.msg)
      } else {
        // success! server sends back user first name
        dispatch({type: types.SET_USER, payload: username})
        dispatch({type: types.SET_FIRST_NAME, payload: json.msg})
        history.push(urls.GRAPH)
      }
    }).catch (err => {
      console.error(err);
    });
  } 

  const keyUpHandler = ({keyCode}) => {
    if(keyCode === keys.ENTER) {
      submit()
    }
  }
  return (
    <Paper className="signin-form">
        <h1 className="title">Sign In</h1>
        { message ? (<p style={{color:"red"}}>{message}</p>) : null}
        <FormControl>
          <label>Username</label>
          <Input 
            className="text-field" 
            placeholder="Enter user name" 
            required
            onChange={e => setUsername(e.target.value)}
            onKeyUp={keyUpHandler}
          />
          <label>Password</label>
          <Input 
            className="text-field" 
            type="password" 
            placeholder="Enter password" 
            required
            onChange={e => setPassword(e.target.value)}
            onKeyUp={keyUpHandler} 
          />
          <Button 
            className="submit" 
            variant="contained" 
            color="primary"
            onClick={() => submit()}>
            Submit
          </Button>
        </FormControl>
    </Paper>
  );
})

export default SigninForm;
