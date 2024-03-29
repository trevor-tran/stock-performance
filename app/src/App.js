import {useEffect, useRef, useState} from 'react';
import { Grid, Paper, styled } from '@mui/material';
import Chart from './components/Chart';
import TopBar from './components/TopBar';
import axios from 'axios';
import dayjs from 'dayjs';

import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import Header from './components/Header';
import Footer from './components/Footer';

import { endOfLastMonth, endOfLastYear } from "./components/utils/date";

import './App.css';

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : '#fff',
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: 'center',
  color: theme.palette.text.secondary,
}));

export default function App() {

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
  const prevTickers = useRef();

  /**
   * HOOKs section
   */

  useEffect(() => {
    if (tickers.length === 0) return;

    // the flag to when to evict the entire stockCache
    let needFreshCache = false;
    // call this url when a new ticker added with start date and end date remain unchanged
    let url = `http://localhost:8080/api/stock/${tickers[tickers.length - 1]}?start=${userInputs.startDate}&end=${userInputs.endDate}`;

    // determine if start date and end date have change
    // if so, need to get data for all tickers
    if (prevStartDate.current !== userInputs.startDate || prevEndDate.current !== userInputs.endDate) {
      url = `http://localhost:8080/api/stock/batch?tickers=${tickers.join()}&start=${userInputs.startDate}&end=${userInputs.endDate}`;
      prevStartDate.current = userInputs.startDate;
      prevEndDate.current = userInputs.endDate;
      needFreshCache = true;
    }

    // if (prevTickers.length > tickers.length) {
    //   return;
    // }

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

      if (needFreshCache) {
        newStockCache = data;
      } else {
        newStockCache = intersectMaps(stockCache, data);
      }
      setStockCache(newStockCache);
    }).catch( err => {
      console.log(err);
    });
  }, [tickers.length, userInputs.startDate, userInputs.endDate]);


  function intersectMaps(map1, map2) {
    let intersection = new Map();
    map1.forEach((v, k) => {
      if(map2.has(k)) {
        intersection.set(k, [...v, ...map2.get(k)]);
      }
    })

    return intersection;
  }

  /**
   * functions section
   */

  function handleUserInputs(valueObj) {
    setUserInputs({...userInputs, ...valueObj});
    let found = tickers.indexOf(valueObj.ticker)
    if (found < 0 && valueObj.ticker.trim().length > 0) {
      const newTickers = [...tickers, valueObj.ticker]
      setTickers(newTickers);
      prevTickers.current = newTickers;
    }
  }


  function handleLegendClick(ticker) {
    const newTickers = tickers.filter( v => v !== ticker);
    setTickers(newTickers);
    prevTickers.current = newTickers;

    // remove ticker data from cache
    const newStockCache = new Map();

    stockCache.forEach((v,k) => {
      const newValue = v.filter(e => e.ticker !== ticker);
      newStockCache.set(k, newValue);
    });

    setStockCache(newStockCache);
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
          <h2>Monthly Growth of Initial Investment Over Time</h2>
          <Chart budget={userInputs.budget} stockData={stockCache} onLegendClick={handleLegendClick}/>
        </Item>
      </Grid>
    </Grid>
  );
}
