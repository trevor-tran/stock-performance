import { useState, useEffect } from 'react'
import { Button, TextField, Autocomplete, Box, CircularProgress } from '@mui/material';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import axios from 'axios';
import dayjs from 'dayjs';

import { endOfLastMonth, endOfLastYear } from "./utils/date";

export default function TopBar(props) {

  const [budget, setBudget] = useState(props.budget);
  const [startDate, setStartDate] = useState(dayjs(props.startDate || endOfLastYear));
  const [endDate, setEndDate] = useState(dayjs(props.endDate || endOfLastMonth));
  const [ticker, setTicker] = useState(props.ticker);
  const [tickerMatches, setTickerMatches] = useState([]);
  const [loading, setLoading] = useState(false);


  useEffect(() => {
    if (!ticker) return;
    setLoading(true);
    const url = "http://localhost:8080/api/symbol?keyword=";
    axios.get(url + ticker
    ).then(response => {
      setTickerMatches(response.data);
      setLoading(false);
    }).catch(error => {
      console.log(error);
      setLoading(false);
    });
  }, [ticker])

  function handleOnUpdate(e) {
    e.preventDefault();
    props.onChange({
      startDate: startDate.format("YYYY-MM-DD"),
      budget: Number(budget),
      endDate: endDate.format("YYYY-MM-DD"),
      ticker
    });

    setTicker("");
  }

  function disableWeekends(date) {
    return date.day() === 0 || date.day() === 6;
  }

  return (
    <>
      <TextField
        type="number"
        label="Budget($)"
        margin="normal"
        variant="outlined"
        value={budget}
        onChange={e => setBudget(e.target.value)}
        inputProps={{
          step: 1000,
          min: 1000
        }}
      />

      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker label="Start Date" value={startDate} views={['month', 'year']}
        onChange={newDate => setStartDate(newDate.endOf("month"))}
        shouldDisableDate={disableWeekends}
        minDate={dayjs(endOfLastMonth).subtract(20, "year")}
        maxDate={dayjs(endOfLastMonth).subtract(1, "month")}
        />
      </LocalizationProvider>

      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker label="End Date" value={endDate} views={['month', 'year']}
        onChange={newDate => setEndDate(newDate.endOf("month"))}
        shouldDisableDate={disableWeekends}
        minDate={dayjs(endOfLastMonth).subtract(19, "year")}
        maxDate={endOfLastMonth}/>
      </LocalizationProvider>

      <Autocomplete
        sx={{ minWidth: "300px", width: "400px" }}
        className="dropbox"
        freeSolo
        options={tickerMatches}
        value={ticker}
        loading={loading}
        getOptionLabel={option => option.ticker ? option.ticker : option}
        renderOption={(props, option) => (
          <Box key={option.ticker} component="li" sx={{  borderBottom: "1px solid #dcdcdc" }} {...props}>
            <span style={{marginRight: "50px"}}>{option.ticker}</span> <span style={{marginLeft: "auto", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap"}}>{option.name}</span>
          </Box>
        )}
        renderInput={(params) => (
          <TextField {...params} label="Search Ticker" margin="normal" variant="outlined"
            InputProps={{
              ...params.InputProps,
              endAdornment: (
                <>
                  {loading ? <CircularProgress color="inherit" size={15} /> : null}
                  {params.InputProps.endAdornment}
                </>
              ),
            }}
          />
        )}
        onInputChange={(e, v) => setTicker(v.toUpperCase())}
      />
      <Button variant="outlined" color="primary" onClick={handleOnUpdate}>Update</Button>
    </>
  )
}
