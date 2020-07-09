import React, { useState, useEffect, useContext } from 'react'
import { Paper, Button, TextField } from '@material-ui/core'
import { Autocomplete } from '@material-ui/lab';
import { MuiPickersUtilsProvider, KeyboardDatePicker } from '@material-ui/pickers'
import DateFnsUtils from '@date-io/date-fns'
import moment from 'moment';

import { Context } from '../store';
import { types } from './utils/Constants';

//css
import "./css/TopBar.css"

const DATE_FORMAT = ("MM/dd/yyyy");
const MAX_START_DATE = moment(Date.now()).subtract(7, 'd');

const ALPHAVANTAGE_KEYS = ['9550BIKHH601BM7H', 'TWMPYRJCSANOW7L7'];

function TopBar() {

  const { state, dispatch } = useContext(Context);

  const [budget, setBudget] = useState(Number(state.budget));
  const [startDate, setStartDate] = useState();
  const [endDate, setEndDate] = useState();
  const [symbol, setSymbol] = useState("");
  const [matches, setMatches] = useState([]);

  useEffect(() => {
    setStartDate(MAX_START_DATE);
    setEndDate(Date.now());
  }, []);

  // this hook will make an API call
  // to a finanicial service to get all of the symbols that best match the input from user.
  useEffect(() => {
    if (!symbol) return;
    const randomInt = getRandomInt(0, 10);
    const url = "http://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + symbol + "&apikey=" + ALPHAVANTAGE_KEYS[randomInt % ALPHAVANTAGE_KEYS.length];
    fetch(url, {
      method: 'POST',
      mode: "cors",
      headers: {
        'Accept': 'application/json'
      }
    }).then(response => {
      if (response.status >= 200 && response.status <= 299) {
        return response.json();
      } else {
        throw "Error in request";
      }
    }).then(json => {
      if (!json["bestMatches"]) {
        throw "This app has a limited number of requests, in minute or day, to a free financial service. Please don't type too many letters in a minute."
      }

      let bestMatches = [];
      json["bestMatches"].forEach(obj => {
        let match = {};
        if (obj["8. currency"] === "USD") {
          match["symbol"] = obj["1. symbol"];
          match["name"] = obj["2. name"];
          bestMatches.push(match);
        }
      });
      setMatches(bestMatches);
    }).catch(err => {
      alert(err);
    });
  }, [symbol]);

  /**
 * Returns a random integer between min (inclusive) and max (inclusive).
 * The value is no lower than min (or the next integer greater than min
 * if min isn't an integer) and no greater than max (or the next integer
 * lower than max if max isn't an integer).
 * Using Math.round() will give you a non-uniform distribution!
 */
  // https://stackoverflow.com/questions/1527803/generating-random-whole-numbers-in-javascript-in-a-specific-range
  function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  // update store by calling dispatches
  const submit = () => {
    if (symbol) {
      const found = matches.findIndex( obj =>
        obj.symbol.toUpperCase() === symbol.toUpperCase()
      );
      if (found === -1) {
        alert("Please choose a stock symbol in the suggested list.")
      } else {
        dispatch({ type: types.ADD_SYMBOL, payload: { symbol: symbol.toUpperCase() } });
      }
    }
    if (budget && Number(budget) > 0) {
      dispatch({ type: types.BUDGET, payload: { budget } });
    }
    dispatch({
      type: types.START_DATE,
      payload: { startDate: moment(startDate).format("YYYY-MM-DD") }
    });
    dispatch({
      type: types.END_DATE,
      payload: { endDate: moment(endDate).format("YYYY-MM-DD") }
    });
    setSymbol("");
  }

  const handleBudgetChange = (e) => {
    const value = e.target.value;
    setBudget(value);
  }

  const handleSymbolSelected = (e, v) => {
    setSymbol(v.symbol);
  }

  const handleSymbolChange = (e, v) => {
    setSymbol(v.toUpperCase());
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
          onChange={handleBudgetChange}
          inputProps={{
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
          maxDate={MAX_START_DATE}
        />

        <KeyboardDatePicker
          disableToolbar
          variant="inline"
          format={DATE_FORMAT}
          margin="normal"
          label="End date"
          value={endDate}
          onChange={date => setEndDate(date)}
          maxDate={Date.now()}
        />

        <Autocomplete
          className="dropbox"
          freeSolo
          disableClearable
          options={matches}
          value={symbol}
          getOptionLabel={option => option.symbol ? option.symbol : option}
          renderOption={option => (
            <React.Fragment>
              {option.symbol} - {option.name}
            </React.Fragment>
          )}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Search symbol"
              margin="normal"
              variant="outlined"
            />
          )}
          onChange={handleSymbolSelected}
          onInputChange={handleSymbolChange}
        />

        <Button className="update-button" variant="outlined" color="primary" onClick={submit}>Update</Button>
      </Paper>
    </MuiPickersUtilsProvider>
  )
}

export default TopBar;