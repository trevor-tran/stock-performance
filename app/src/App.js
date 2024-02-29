import { Grid, Paper, styled } from '@mui/material';
import './App.css';
import Chart from './components/Chart';
import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import Header from './components/Header';
import TopBar from './components/TopBar';

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: 'center',
  color: theme.palette.text.secondary,
}));

function App() {
  return (
    <Grid container sx={{ width: "70vw", height: "100vh", margin: "auto" }}>
      <Grid item sx={{width: "100%"}}>
        <Item sx={{display: "flex", justifyContent: "space-between", alignItems: "center"}}>
          <TopBar />
        </Item>
      </Grid>
      <Grid item sx={{width: "100%"}}>
        <Item sx={{margin: 'auto', height: "500px" }}>
          <Chart />
        </Item>
      </Grid>
    </Grid>
  );
}

export default App;
