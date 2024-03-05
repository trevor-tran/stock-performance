import {useEffect, useRef, useState} from 'react';
import { Grid, Paper, styled } from '@mui/material';
import './App.css';
import Chart from './components/Chart';
import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import Header from './components/Header';
import TopBar from './components/TopBar';
import axios from 'axios';

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: 'center',
  color: theme.palette.text.secondary,
}));

function App() {

  const [userInputs, setUserInputs] = useState({
    budget: 1000,
    startDate: "",
    endDate: "",
    ticker: ""
  })
  const [tickers, setTickers] = useState([]);
  const [loading, setLoading] = useState(false);

  const [stockCache, setStockCache] = useState(new Map());

  const prevStartDate = useRef(userInputs.startDate);
  const prevEndDate = useRef(userInputs.endDate);

  useEffect(() => {
    if (tickers.length === 0) return;

    let isCacheDirty = false;

    let url = `http://localhost:8080/api/stock/${tickers[tickers.length - 1]}?start=${userInputs.startDate}&end=${userInputs.endDate}`;

    if (prevStartDate.current !== userInputs.startDate || prevEndDate.current !== userInputs.endDate) {
      url = `http://localhost:8080/api/stock/batch?tickers=${tickers.join()}&start=${userInputs.startDate}&end=${userInputs.endDate}`;
      prevStartDate.current = userInputs.startDate;
      prevEndDate.current = userInputs.endDate;
      isCacheDirty = true;
    }

    axios.get(url).then(response => {
      let normalizedData = new Map();
      response.data.forEach(e => {
        const {id, endOfMonthPrice, dividend} = e;
        if (!normalizedData.has(id.date)) {
          normalizedData.set(id.date, []);
        }
        const ticker = id.ticker;
        normalizedData.get(id.date).push({ticker, endOfMonthPrice, dividend});
      });

      return normalizedData;

    }).then(data => {
      let newStockCache;

      if (isCacheDirty) {
        newStockCache = data;
      } else {
        newStockCache = intersectMaps(stockCache, data);
      }
      setStockCache(newStockCache);
    });
  }, [tickers.length, userInputs.startDate, userInputs.endDate]);

  function intersectMaps(map1, map2) {
    const map1KeySet = new Set(map1.keys());
    const map2KeySet = new Set(map2.keys());
    const mutualKeySet = map1KeySet.intersection(map2KeySet);

    let intersection = new Map();

    mutualKeySet.forEach(k => {
      const v = [...map1.get(k), ...map2.get(k)];
      intersection.set(k, v);
    });
    
    return intersection;
  }

  function handleUserInputs(valueObj) {
    setUserInputs({...userInputs, ...valueObj});
    let found = tickers.indexOf(valueObj.ticker)
    if (found < 0 && valueObj.ticker.trim().length > 0) {
      setTickers([...tickers, valueObj.ticker]);
    }
  }

  return (
    <Grid container sx={{ width: "70vw", height: "100vh", margin: "auto" }}>
      <Grid item sx={{width: "100%"}}>
        <Item sx={{display: "flex", justifyContent: "space-between", alignItems: "center"}}>
          <TopBar startDate={userInputs.startDate} endDate={userInputs.endDate} budget={userInputs.budget} ticker={userInputs.ticker} onChange={handleUserInputs}/>
        </Item>
      </Grid>
      <Grid item sx={{width: "100%"}}>
        <Item sx={{margin: 'auto', height: "500px" }}>
          <Chart budget={userInputs.budget} stockData={stockCache}/>
        </Item>
      </Grid>
    </Grid>
  );
}

export default App;
