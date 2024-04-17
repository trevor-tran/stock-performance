import { useState, useEffect } from 'react'
import { Button, TextField, Autocomplete, Box, CircularProgress, InputAdornment } from '@mui/material';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import axios from 'axios';
import dayjs from 'dayjs';
import { useFormik } from 'formik';
import * as yup from 'yup';

import AutoHideSnackBar from './AutoHideSnackBar';

import { endOfLastMonth, DATE_FORMAT, endOfMonth } from "../utils/date";
import { HOST, MAXIMUM_TICKERS } from "../utils/utils";

// TopBar component
export default function TopBar(props) {

  // validation schema
  const validationSchema = yup.object({
    budget: yup
      .number('Enter amount')
      .integer()
      .positive("Positive number")
      .max(1000000, 'Less than 1,000,000')
      .required('Required'),
    startDate: yup
      .string()
      .matches(/^\d{4}-\d{2}-\d{2}$/, "Invalid format")
      .required('Required'),
    endDate: yup
      .string()
      .matches(/^\d{4}-\d{2}-\d{2}$/, "Invalid format")
      .required('Required'),
    ticker: props.tickers.length === 0 ?
      yup.string().required('Required')
      :
      yup.string()
  });

  const [tickerMatches, setTickerMatches] = useState([]);
  const [loading, setLoading] = useState(false);

  const [notification, setNotification] = useState({
    message: "",
    severity: "error",
  });

  const formik = useFormik({
    initialValues: {
      budget: props.budget,
      startDate: props.startDate,
      endDate: props.endDate,
      ticker: props.ticker,
    },
    validationSchema: validationSchema,
    onSubmit: values => {
      const { startDate, budget, endDate, ticker } = values;

      // check if the ticker is already in the list
      if (props.tickers.includes(ticker)) {
        setNotification({
          ...notification,
          message: `${ticker} already added`,
          severity: "info"
        });

        return;
      }

      if (ticker && props.tickers.length === MAXIMUM_TICKERS) {
        resetSomeFields();

        setNotification({
          ...notification,
          message: `You can only add up to ${MAXIMUM_TICKERS} tickers`,
          severity: "warning"
        });

        return;
      }

      if (!dayjs(startDate).isBefore(endDate)) {
        formik.setErrors({
          startDate: "Must be BEFORE end date",
          endDate: "Must be AFTER start date"
        });
        return;
      }

      // check if the ticker is valid
      // ticker must be in the list of tickerMatches
      const foundMatch = tickerMatches.find(t => t.ticker.toUpperCase() === ticker.toUpperCase());
      if (ticker.length > 0 && !foundMatch) {
        setNotification({
          ...notification,
          message: "Please select a ticker from the dropdown list",
          severity: "error"
        });
        return;
      }

      props.onChange({
        startDate: startDate,
        budget: Number(budget),
        endDate: endDate,
        ticker: ticker
      });

      resetSomeFields();
    }
  });

  // get ticker matches
  useEffect(() => {
    const ticker = formik.values.ticker;

    if (!ticker) {
      setTickerMatches([]);
      return;
    }

    setLoading(true);

    const url = HOST + "/api/symbol?keyword=" + ticker;
    axios.get(url
    ).then(response => {
      setTickerMatches(response.data);
      setLoading(false);
    }).catch(error => {
      const errJson = error.toJSON();
      if (errJson.status === 509) {
        setNotification({
          ...notification,
          message: "We've hit our ticker searching limit. Please try again in a minute. Thanks for your patience!",
          severity: "error"
        });
      }
      setLoading(false);
    });

  }, [formik.values.ticker])


  // check if the date is weekend
  function isWeekend(date) {
    return date.day() === 0 || date.day() === 6;
  }

  function resetSomeFields() {
    //reset some fields
    formik.resetForm({ values: { ...formik.values, ticker: "" } });
    setTickerMatches([]);
  }



  return (
    <>
      <Box className="col-12 col-lg-2 col-xxl-1">
        <TextField
          required
          type="number"
          label="Budget"
          margin="normal"
          variant="outlined"
          name="budget"
          value={formik.values.budget}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.budget && Boolean(formik.errors.budget)}
          helperText={(formik.touched.budget && formik.errors.budget) || " "}
          inputProps={{
            step: 1000,
          }}
          InputLabelProps={{ shrink: true }}
          sx={{ width: "100%" }}
          InputProps={{
            startAdornment: <InputAdornment position="start">$</InputAdornment>,
          }}
        />
      </Box>
      <Box className="col-12 col-lg-2 col-xxl-2 px-lg-0" sx={{ paddingTop: "16px" }}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DatePicker label="Start Date"
            format='MM YYYY'
            views={['month', 'year']}
            value={dayjs(formik.values.startDate)}
            onChange={newDate => { formik.setFieldValue("startDate", endOfMonth(newDate, DATE_FORMAT.ISO_8601)) }}
            shouldDisableDate={isWeekend}
            minDate={dayjs(endOfLastMonth).subtract(20, "year")}
            maxDate={dayjs(endOfLastMonth)}
            slotProps={{
              textField: {
                required: true,
                InputLabelProps: { shrink: true },
                name: "startDate",
                onBlur: formik.handleBlur,
                helperText: (formik.touched.startDate && formik.errors.startDate) || " ",
                error: Boolean(formik.touched.startDate) && Boolean(formik.errors.startDate)
              }
            }}
            sx={{ width: "100%" }}
          />
        </LocalizationProvider>
      </Box>
      <Box className="col-12 col-lg-2 col-xxl-2" sx={{ paddingTop: "16px" }}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DatePicker label="End Date"
            format='MM YYYY'
            views={['month', 'year']}
            value={dayjs(formik.values.endDate)}
            onChange={newDate => { formik.setFieldValue("endDate", endOfMonth(newDate, DATE_FORMAT.ISO_8601)) }}
            shouldDisableDate={isWeekend}
            minDate={dayjs(endOfLastMonth).subtract(19, "year")}
            maxDate={dayjs(endOfLastMonth)}
            slotProps={{
              textField: {
                required: true,
                InputLabelProps: { shrink: true },
                name: "endDate",
                onBlur: formik.handleBlur,
                helperText: (formik.touched.endDate && formik.errors.endDate) || " ",
                error: Boolean(formik.touched.endDate) && Boolean(formik.errors.endDate),
              }
            }}
            sx={{ width: "100%" }}
          />
        </LocalizationProvider>
      </Box>
      <Box className="col-12 col-lg col-xl-3 col-xxl-2 px-lg-0">
        <Autocomplete
          className="dropbox"
          freeSolo
          options={tickerMatches}
          value={formik.values.ticker}
          loading={loading}
          getOptionLabel={option => option.ticker ? option.ticker : option}
          renderOption={(props, option) => (
            <Box key={option.ticker} component="li" sx={{ borderBottom: "1px solid #dcdcdc" }} {...props}>
              <span style={{ marginRight: "50px" }}>{option.ticker}</span>
              <span style={{ marginLeft: "auto", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{option.name}</span>
            </Box>
          )}
          renderInput={(params) => (
            <TextField {...params}
              required={props.tickers.length === 0}
              label="Search Ticker" margin="normal" variant="outlined" placeholder="e.g. AAPL"
              onBlur={formik.handleBlur}
              name="ticker"
              error={formik.touched.ticker && Boolean(formik.errors.ticker)}
              helperText={(formik.touched.ticker && formik.errors.ticker) || " "}
              InputLabelProps={{ shrink: true }}
              InputProps={{
                ...params.InputProps,
                endAdornment: (
                  <>
                    {loading ? <CircularProgress color="inherit" size={15} /> : null}
                    {params.InputProps.endAdornment}
                  </>
                ),
              }}
              sx={{ width: "100%" }}
            />
          )}
          onInputChange={(e, v) => formik.setFieldValue("ticker", v.toUpperCase().trim())}
          sx={{ width: "100%" }}
        />
      </Box>
      <Box className="col-12 col-lg-1">
        <Button variant="contained" color="primary"
          sx={{ width: "100%", height: "56px", marginTop: "16px" }}
          onClick={formik.handleSubmit}
        >
          <span className="fw-bold">Update</span>
        </Button>
      </Box>
      {notification.message.length > 0 &&
        <AutoHideSnackBar
          open
          message={notification.message}
          severity={notification.severity}
          onHide={() => setNotification({ ...notification, message: "" })} />}
    </>
  )
}
