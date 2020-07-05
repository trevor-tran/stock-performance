import React, { useState, useEffect, useContext } from 'react'
import { Paper, Button, TextField } from '@material-ui/core'
import { MuiPickersUtilsProvider, KeyboardDatePicker } from '@material-ui/pickers'
import DateFnsUtils from '@date-io/date-fns'
import moment from 'moment';

import { Context } from '../store';
import { types} from './utils/Constants'

const DATE_FORMAT = ("MM/dd/yyyy");
const MAX_START_DATE = moment(Date.now()).subtract(7,'d');

function TopBar() {

  const {state, dispatch } = useContext(Context);

  const [budget, setBudget] = useState(0);
  const [startDate, setStartDate] = useState();
  const [endDate, setEndDate] = useState();
  const [symbol, setSymbol] = useState("");

  useEffect(() => {
    setStartDate(MAX_START_DATE);
    setEndDate(Date.now());
    setBudget(Number(state.budget));
  }, []);

  const submit = () => {
    if (symbol) {
      dispatch({ type: types.ADD_SYMBOL, payload: { symbol: symbol.toUpperCase() }});
    }

    if (budget && Number(budget) > 0) {
      dispatch({ type: types.BUDGET, payload: {budget}});
    }

    dispatch ({
      type: types.START_DATE,
      payload: {startDate: moment(startDate).format("YYYY-MM-DD")}
    });

    dispatch ({
      type: types.END_DATE,
      payload: {endDate: moment(endDate).format("YYYY-MM-DD")}
    });

    setSymbol("");
  }

  const handleChange = (e) => {
    const name = e.target.name;
    const value = e.target.value;
    if (name === 'budget') {
      setBudget(value);
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
          inputProps = {{
            step: 10,
            min: 1
          }}
        />

        <KeyboardDatePicker
          disableToolbar
          variant="inline"
          format={DATE_FORMAT}
          margin="normal"
          label="Start date"
          value={startDate}
          onChange={date => setStartDate(date)}
          maxDate = {MAX_START_DATE}
        />

        <KeyboardDatePicker
          disableToolbar
          variant="inline"
          format={DATE_FORMAT}
          margin="normal"
          label="End date"
          value={endDate}
          onChange={date => setEndDate(date)}
          maxDate = {Date.now()}
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