import { useState, useEffect } from 'react'
import { Button, TextField, Autocomplete, Box, CircularProgress } from '@mui/material';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import axios from 'axios';
import dayjs from 'dayjs';
import { useFormik } from 'formik';
import * as yup from 'yup';

import { endOfLastMonth, DATE_FORMAT, endOfMonth } from "./utils/date";

// validation schema
const validationSchema = yup.object({
  budget: yup
    .number('enter investment amount')
    .integer()
    .positive("must be positive number")
    .max(1000000, 'must be less than 1,000,000')
    .required('is required'),
  startDate: yup
    .string()
    .matches(/^\d{4}-\d{2}-\d{2}$/, "invalid date format")
    .required('is required'),
  endDate: yup
    .string()
    .matches(/^\d{4}-\d{2}-\d{2}$/, "invalid date format")
    .required('is required'),
  ticker: yup
    .string()
    .matches(/^[A-Z]+$/, "must be uppercase")
    .max(5, "must be less than 5 characters")
    .required('is required')
});

// TopBar component
export default function TopBar(props) {

  const [tickerMatches, setTickerMatches] = useState([]);
  const [loading, setLoading] = useState(false);

  const formik = useFormik({
    initialValues: {
      budget: props.budget,
      startDate: props.startDate,
      endDate: props.endDate,
      ticker: props.ticker
    },
    validationSchema: validationSchema,
    onSubmit: values => {
      const { startDate, budget, endDate, ticker } = values;

      if (dayjs(startDate).isBefore(endDate)) {
        props.onChange({
          startDate: startDate,
          budget: Number(budget),
          endDate: endDate,
          ticker: ticker
        });

        //reset some fields
        formik.resetForm({values: {...values, ticker: ""}});
        setTickerMatches([]);

      } else {
        console.log("start date must be before end date");
        formik.setErrors({
          startDate: "must be BEFORE end date",
          endDate: "must be AFTER start date"
        });
      }
    }
  });

 // get ticker matches
  useEffect(() => {
    const ticker = formik.values.ticker;

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
  }, [formik.values.ticker])


  // check if the date is weekend
  function isWeekend(date) {
    return date.day() === 0 || date.day() === 6;
  }



  return (
    <>
      <Box>
        <TextField
          required
          type="number"
          label="Budget($)"
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
        />
      </Box>
      <Box style={{ marginTop: "16px", marginBottom: "8px" }}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DatePicker label="Start Date"
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
          />
        </LocalizationProvider>
      </Box>
      <Box style={{ marginTop: "16px", marginBottom: "8px" }}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DatePicker label="End Date"
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
                error: Boolean(formik.touched.endDate) && Boolean(formik.errors.endDate)
              }
            }}
          />
        </LocalizationProvider>
      </Box>
      <Box>
        <Autocomplete
          sx={{ minWidth: "300px", width: "400px" }}
          className="dropbox"
          freeSolo
          options={tickerMatches}
          value={formik.values.ticker}
          loading={loading}
          getOptionLabel={option => option.ticker ? option.ticker : option}
          renderOption={(props, option) => (
            <Box key={option.ticker} component="li" sx={{ borderBottom: "1px solid #dcdcdc" }} {...props}>
              <span style={{ marginRight: "50px" }}>{option.ticker}</span> <span style={{ marginLeft: "auto", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{option.name}</span>
            </Box>
          )}
          renderInput={(params) => (
            <TextField required {...params} label="Search Ticker" margin="normal" variant="outlined" placeholder="e.g. AAPL"
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
            />
          )}
          onInputChange={(e, v) => formik.setFieldValue("ticker", v.toUpperCase())}
        />
      </Box>
      <Button variant="contained" color="primary" sx={{ height: "56px", marginTop: "16px" }} onClick={formik.handleSubmit}>Update</Button>
    </>
  )
}
