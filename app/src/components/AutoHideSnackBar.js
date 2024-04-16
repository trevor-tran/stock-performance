import { useState } from 'react';
import {
  Snackbar,
  Alert
} from '@mui/material';

export default function AutoHideSnackBar(props) {
  const [open, setOpen] = useState(props.open);

  const handleClose = () => {
    setOpen(false);
    props.onHide();
  }

  return (
    <Snackbar
      anchorOrigin={{ vertical: "top", horizontal: "center" }}
      open={open}
      autoHideDuration={props.autoHideDuration || 5000}
      onClose={handleClose}
      message={props.message}>
      <Alert
        onClose={handleClose}
        severity={props.severity || "success"}
        variant="filled">
        {props.message}
      </Alert>
    </Snackbar>
  );
}