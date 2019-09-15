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
  const initialState = {
    username: null,
    password: null,
    message: null,
    is_submiting: false
  }
  const [data, setData] = useState(initialState);


  const submit = () => {
    setData({...data, is_submiting:true})
    let url = urls.SERVER_URL + urls.SIGNIN;
    fetch(url, {
      method: 'POST',
      body: JSON.stringify({
        'username': data.username,
        'password': data.password
      })
    }).then (response => {
      if (response.ok) {
        return response.json();
      }
      throw response
    }).then (json => {
      if (json.success) {
        dispatch({type: types.LOGIN, payload: {user: data.username, token:"token", first_name: json.msg}})
        history.push(urls.GRAPH)
      } else {
        throw json
      }
    }).catch (err => {
      setData({...data, is_submiting: false, message: err.msg || err.statusText})
    });
  }
  
  const handleInputChange = (e) => {
    setData({...data, [e.target.name]: e.target.value})
  }

  const handleKeyUp = ({keyCode}) => {
    if(keyCode === keys.ENTER) {
      submit()
    }
  }
  return (
    <Paper className="signin-form">
        <h1 className="title">Sign In</h1>
        { data.message ? (<p style={{color:"red"}}>{data.message}</p>) : null}
        <FormControl>
          <label>Username</label>
          <Input 
            name="username"
            className="text-field" 
            placeholder="Enter user name" 
            required
            onChange={handleInputChange}
            onKeyUp={handleKeyUp}
          />
          <label>Password</label>
          <Input 
          name="password"
            className="text-field" 
            type="password" 
            placeholder="Enter password" 
            required
            onChange={handleInputChange}
            onKeyUp={handleKeyUp} 
          />
          <Button 
            disabled={data.is_submiting}
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
