import React, { useState, useEffect, useContext } from 'react'
import { Paper, Button, TextField, Autocomplete } from '@mui/material';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';


//css

const DATE_FORMAT = ("mm/dd/yyyy");

const ALPHAVANTAGE_KEYS = ['9550BIKHH601BM7H', 'TWMPYRJCSANOW7L7'];

export default function TopBar() {


  const [budget, setBudget] = useState(0);
  const [startDate, setStartDate] = useState();
  const [endDate, setEndDate] = useState(Date.now() - 1);
  const [symbol, setSymbol] = useState("");
  const [symbolMatches, setSymbolMatches] = useState([]);

  useEffect(() => {
    setEndDate(Date.now() - 1);
  }, []);

  // this hook will make an API call
  // to a finanicial service to get all of the symbols that best match the input from user.
  useEffect(() => {
    if (!symbol) return;
    const url = "http://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + symbol + "&apikey=" + ALPHAVANTAGE_KEYS[0];
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
      setSymbolMatches(bestMatches);
    }).catch(err => {
      alert(err);
    });
  }, [symbol]);


  return (
    <Paper className="top-bar">

      <TextField
        type="number"
        label="Budget($)"
        margin="normal"
        variant="outlined"
        value={budget}
        onChange={e => setBudget(e.target.value)}
        inputProps={{
          step: 10,
          min: 10
        }}
      />

      <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DatePicker label="Start Date" />
      </LocalizationProvider>

      <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DatePicker label="End Date" />
      </LocalizationProvider>

      <Autocomplete
        className="dropbox"
        freeSolo
        disableClearable
        options={symbolMatches}
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
      />

      <Button className="update-button" variant="outlined" color="primary">Update</Button>
    </Paper>
  )
}
