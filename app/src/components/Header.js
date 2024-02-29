import { useState } from "react";
import {
  Button,
  Typography,
  Toolbar,
  AppBar,
  FormControl,
  Dialog
} from '@mui/material';

import SignUp from './SignUp';
import SignIn from "./SignIn";


export default function Header() {
  const [openSignUp, setOpenSignUp] = useState(false);
  const [openSignIn, setOpenSignIn] = useState(false);
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6">Stock Performance</Typography>
        <FormControl id="menu">
          <Button className="button" color="inherit" onClick={() => setOpenSignIn(true)}>Sign In</Button>

          <Dialog open={openSignUp} onClose={() => setOpenSignUp(false)}><SignUp/></Dialog>
          <Dialog open={openSignIn} onClose={() => setOpenSignIn(false)}><SignIn/></Dialog>
        </FormControl>
      </Toolbar>
    </AppBar>
  );
}