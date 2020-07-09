import React, { useState, useContext } from 'react';
import { Paper, Input, Button } from '@material-ui/core';
import { withRouter } from 'react-router-dom'
import { Context } from '../store'

// constants
import { types, urls, keys } from './utils/Constants'
// css
import './css/Form.css'

const SigninForm = withRouter(({ history }) => {
  const { dispatch } = useContext(Context)
  const initialState = {
    username: null,
    password: null,
    message: null,
    is_submiting: false
  }
  const [data, setData] = useState(initialState);


  const submit = () => {
    setData({ ...data, is_submiting: true })
    let url = urls.SERVER_URL + urls.SIGNIN;
    fetch(url, {
      method: 'POST',
      body: JSON.stringify({
        'username': data.username,
        'password': data.password
      })
    }).then(response => {
      if (response.ok) {
        return response.json();
      }
      throw response
    }).then(json => {
      if (json.success) {
        dispatch({ type: types.LOGIN, payload: { user: data.username, first_name: json.msg } })
        history.push(urls.GRAPH)
      } else {
        throw json
      }
    }).catch(err => {
      setData({ ...data, is_submiting: false, message: err.msg || err.statusText })
    });
  }

  const handleInputChange = (e) => {
    setData({ ...data, [e.target.name]: e.target.value })
  }

  const handleKeyUp = ({ keyCode }) => {
    if (keyCode === keys.ENTER) {
      submit()
    }
  }
  return (
    <Paper className="signin-form">
      <h1 className="title">Sign In</h1>
      {data.message ? (<p style={{ color: "red" }}>{data.message}</p>) : null}
      <div className="label-input-container">
        <label>Username</label>
        <Input
          name="username"
          className="text-field"
          placeholder="username"
          required
          onChange={handleInputChange}
          onKeyUp={handleKeyUp}
          style={{ float: "left" }}
        />
      </div>
      <div className="label-input-container">
        <label>Password</label>
        <Input
          name="password"
          className="text-field"
          type="password"
          placeholder="password"
          required
          onChange={handleInputChange}
          onKeyUp={handleKeyUp}
        />
      </div>
      <Button
        style={{marginTop: "10px", marginBottom: "5px"}}
        disabled={data.is_submiting}
        className="submit-button"
        variant="contained"
        color="primary"
        onClick={() => submit()}>
        Submit
      </Button>
    </Paper>
  );
})

export default SigninForm;
