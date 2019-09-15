import React from 'react'
import { Paper, Button, TextField } from '@material-ui/core'
import { MuiPickersUtilsProvider, KeyboardDatePicker } from '@material-ui/pickers'
import DateFnsUtils from '@date-io/date-fns'


function TopBar() {

  const submit = () => {
    console.log("submited")
  }

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Paper className="top-bar">

        <TextField id="budget" label="Budget($)" margin="normal" variant="outlined" />

        <KeyboardDatePicker
          disableToolbar
          variant="inline"
          format="MM/dd/yyyy"
          margin="normal"
          label="Start date"
        />

        <KeyboardDatePicker
          disableToolbar
          variant="inline"
          format="MM/dd/yyyy"
          margin="normal"
          label="End date"
        />
        
        <TextField id="symbol" label="Symbol:" margin="normal" variant="outlined" />

        <Button variant="outlined" color="primary" onClick={submit}>Update</Button>
      </Paper>
    </MuiPickersUtilsProvider>
  )
}

export default TopBar;