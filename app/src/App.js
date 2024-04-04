import { useEffect, useRef, useState } from 'react';
import { Box, Grid, Paper, styled } from '@mui/material';
import Chart from './components/Chart';
import TopBar from './components/TopBar';
import axios from 'axios';

import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import Header from './components/Header';
import Footer from './components/Footer';
import News from './components/News';

import "./assets/css/App.css";


const HOST = "http://localhost:8080";

export default function App() {

  const [userInputs, setUserInputs] = useState({
    budget: 1000,
    startDate: "",
    endDate: "",
    ticker: ""
  })
  const [tickers, setTickers] = useState([]);
  const [newsList, setNewsList] = useState([]);
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
    let url = `${HOST}/api/stock/${tickers[tickers.length - 1]}?start=${userInputs.startDate}&end=${userInputs.endDate}`;
    // determine if start date and end date have change
    // if so, need to get data for all tickers
    if (prevStartDate.current !== userInputs.startDate || prevEndDate.current !== userInputs.endDate) {
      url = `${HOST}/api/stock/batch?tickers=${tickers.join()}&start=${userInputs.startDate}&end=${userInputs.endDate}`;
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
        const { id, endOfMonthPrice, dividend } = e;
        if (!normalizedData.has(id.date)) {
          normalizedData.set(id.date, []);
        }
        const ticker = id.ticker;
        normalizedData.get(id.date).push({ ticker, endOfMonthPrice, dividend });
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
    }).catch(err => {
      console.log(err);
    });
  }, [tickers.length, userInputs.startDate, userInputs.endDate]);


  useEffect(() => {
    if (tickers.length === 0) return;
    const url = `${HOST}/api/news?tickers=${tickers.join(",")}&size=${20}`;
    axios.get( url
      ).then(response => {
        console.log(response.data)
        setNewsList(response.data);
      }).catch(err => {
        console.log(err);
      });
  }, [tickers.length]);

  function intersectMaps(map1, map2) {
    let intersection = new Map();
    map1.forEach((v, k) => {
      if (map2.has(k)) {
        intersection.set(k, [...v, ...map2.get(k)]);
      }
    })

    return intersection;
  }

  /**
   * functions section
   */

  function handleUserInputs(valueObj) {
    setUserInputs({ ...userInputs, ...valueObj });
    let found = tickers.indexOf(valueObj.ticker)
    if (found < 0 && valueObj.ticker.trim().length > 0) {
      const newTickers = [...tickers, valueObj.ticker]
      setTickers(newTickers);
      prevTickers.current = newTickers;
    }
  }


  function handleLegendClick(ticker) {
    const newTickers = tickers.filter(v => v !== ticker);
    setTickers(newTickers);
    prevTickers.current = newTickers;

    // remove ticker data from cache
    const newStockCache = new Map();

    stockCache.forEach((v, k) => {
      const newValue = v.filter(e => e.ticker !== ticker);
      newStockCache.set(k, newValue);
    });

    setStockCache(newStockCache);
  }



  return (
    <Box className="container" sx={{ width: "70vw", height: "100vh", margin: "auto" }}>
      <Box className="row">
        <Box className="col" sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <TopBar startDate={userInputs.startDate} endDate={userInputs.endDate} budget={userInputs.budget} ticker={userInputs.ticker} onChange={handleUserInputs} />
        </Box>
      </Box>

      <Box className="row">
        <Box className="col text-center">
        {
          tickers.length === 0 ?
            <Box className="d-flex flex-column justify-content-center align-items-center">
              <img src={process.env.PUBLIC_URL + "/no-data.png"} />
              <p className="h2 font-weight-bold"> No Data Available </p>
              <p className="small text-center text-secondary">There is no data to show you right now.</p>
            </Box>
            :
            <Box sx={{ margin: 'auto', height: "600px", paddingBottom: "40px" }}>
              <p className="h5">Monthly Growth of Initial Investment Over Time</p>
              <Chart budget={userInputs.budget} stockData={stockCache} onLegendClick={handleLegendClick} />
            </Box>
        }
        </Box>
      </Box>
      <Box className="row">
        <Box className="col">
        <div style={{width: "100%", color: "black", border: "1px double black", marginTop: "30px"}}/>
          <p className="h3 fw-bold my-3">Related News</p>
        {
          newsList.map(news =>
            <Box key={news.url}>
            <News title={news.title} url={news.url} imageUrl={news.imageUrl} summary={news.summary} publishedDate={news.publishedDate}/>
            <hr/>
            </Box>
          )
        }
        </Box>
      </Box>
    </Box>
  );
}
