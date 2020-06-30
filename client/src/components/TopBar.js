import React, { useState, useEffect, useContext } from 'react'
import { Paper, Button, TextField } from '@material-ui/core'
import { MuiPickersUtilsProvider, KeyboardDatePicker } from '@material-ui/pickers'
import DateFnsUtils from '@date-io/date-fns'
import moment from 'moment';

import { Context } from '../store';
import { types} from './utils/Constants'

const DATE_FORMAT = ("MM/dd/yyyy");

function TopBar() {

  const { dispatch } = useContext(Context);

  const [budget, setBudget] = useState("");
  const [start, setStart] = useState();
  const [end, setEnd] = useState();
  const [symbol, setSymbol] = useState("");





  useEffect(() => {
    setStart(Date.now());
    setEnd(Date.now());
  }, []);

  const convertToDate = (ms) => {
    return moment(ms).format("L");
  }

  const submit = () => {
    dispatch({ type: types.ADD_SYMBOL, payload: { symbol } });
    setSymbol("");
  }

  const handleChange = (e) => {
    const name = e.target.name;
    const value = e.target.value;
    if (name === 'budget') {
      setBudget(value);
    } else if (name === 'start') {
      console.log(value)
      setStart(moment(value, DATE_FORMAT).unix())
    } else if (name === 'end') {
      console.log(moment(value, DATE_FORMAT).unix())
      setEnd(moment(value, DATE_FORMAT).unix());
    } else if (name === 'symbol') {
      setSymbol(value);
    }
  }

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Paper className="top-bar">

        <TextField
          id="budget"
          type="number"
          name="budget"
          label="Budget($)"
          margin="normal"
          variant="outlined"
          value={budget}
          onChange={handleChange}
        />

        <KeyboardDatePicker
          disableToolbar
          variant="inline"
          format={DATE_FORMAT}
          margin="normal"
          label="Start date"
          value={start}
          onChange={date => setStart(date)}
        />

        <KeyboardDatePicker
          disableToolbar
          variant="inline"
          format={DATE_FORMAT}
          margin="normal"
          label="End date"
          value={end}
          onChange={date => setEnd(date)}
        />

        <TextField
          id="symbol"
          name="symbol"
          label="Symbol:"
          margin="normal"
          variant="outlined"
          value={symbol}
          onChange={handleChange}
        />

        <Button variant="outlined" color="primary" onClick={submit}>Update</Button>
      </Paper>
    </MuiPickersUtilsProvider>
  )
}

export default TopBar;