import { useEffect, useRef, useState } from 'react';
import { Box, Tabs, Tab } from '@mui/material';
import { useQueryClient } from '@tanstack/react-query';
import axios from 'axios';

import Chart from './components/Chart';
import TopBar from './components/TopBar';
import Header from './components/Header';
import Footer from './components/Footer';
import News from './components/News';
import Carousel from './components/Carousel';
import AutoHideSnackBar from './components/AutoHideSnackBar';

import { HOST, STOCK_STALE_TIME_IN_HOURS } from "./utils/utils";


const fetchStockData = async (tickers, startDate, endDate) => {
  const url = `${HOST}/api/stock/batch?tickers=${tickers.join()}&start=${startDate}&end=${endDate}`;
  const response = await axios.get(url);
  return response.data;
}


export default function MainPage() {

  // react-query hook
  const queryClient = useQueryClient();

  const [userInputs, setUserInputs] = useState({
    budget: 0,
    startDate: "",
    endDate: "",
    ticker: ""
  })

  const [tickers, setTickers] = useState([]);
  const [newsList, setNewsList] = useState([]);

  const [stockCache, setStockCache] = useState(new Map());

  const prevStartDate = useRef(userInputs.startDate);
  const prevEndDate = useRef(userInputs.endDate);
  const prevTickers = useRef();

  // tab selection
  const [selectTab, setSelectTab] = useState(0);

  const [topGainers, setTopGainers] = useState([]);
  const [topLosers, setTopLosers] = useState([]);

  const [notification, setNotification] = useState({
    message: "",
    severity: "info",
    autoHideDuration: 10000
  });

  /**
   * HOOKs section
   */

  // get top gainers and top losers
  useEffect(() => {
    // get top gainers
    axios.get(`${HOST}/api/top-stock/gainers`
    ).then(response => {
      setTopGainers(response.data);
    }).catch(err => {
      console.error(err);
    });

    // get top losers
    axios.get(`${HOST}/api/top-stock/losers`
    ).then(response => {
      setTopLosers(response.data);
    }).catch(err => {
      console.error(err);
    });
  }, []);

  // get stock data when tickers and date range change
  useEffect(() => {
    const { startDate, endDate } = userInputs;
    if (tickers.length === 0) return;

    // the flag to when to evict the entire stockCache
    let needFreshCache = false;

    // get stock data for new ticker when start date and end date remain unchanged
    let tickersParam = [tickers[tickers.length - 1]];

    // if start date and end date have change, need to get data for all tickers
    if (prevStartDate.current !== startDate || prevEndDate.current !== endDate) {
      tickersParam = tickers;
      prevStartDate.current = startDate;
      prevEndDate.current = endDate;
      needFreshCache = true;
    }

    // define the function to fetch data and set the stockCache
    async function fetchData() {
      const data = await queryClient.fetchQuery({
        queryKey: [...tickersParam, startDate, endDate],
        queryFn: () => fetchStockData(tickersParam, startDate, endDate),
        staleTime: STOCK_STALE_TIME_IN_HOURS * 3600 * 1000,
      });
      const normalizedResponse = normalizeResponseData(data);

      let newStockCache;
      if (needFreshCache) {
        newStockCache = normalizedResponse;
      } else {
        newStockCache = intersectMaps(stockCache, normalizedResponse);
      }
      setStockCache(newStockCache);
    }

    try {
      fetchData();
    } catch (error) {
      console.error(error);
      let message = "An error occured. Please try again in a minute. Thanks for your patience!";

      // if it's axios error
      if (axios.isAxiosError(error)) {
        const errJson = error.toJSON();
        if (errJson.status === 509) {
          message = "Cannot fetch data due to a high volume of traffic. Please try again in a minute";
        }
      }

      setNotification({
        ...notification,
        message,
        severity: "error",
        autoHideDuration: 10000
      });
    }
  }, [tickers.length, userInputs.startDate, userInputs.endDate]);


  function normalizeResponseData(responseData) {
    let normalizedData = new Map();
    responseData.forEach(e => {
      const { id, endOfMonthPrice, dividend } = e;
      if (!normalizedData.has(id.date)) {
        normalizedData.set(id.date, []);
      }
      const ticker = id.ticker;
      normalizedData.get(id.date).push({ ticker, endOfMonthPrice, dividend });
    });

    return normalizedData;
  }

  // get news when tickers change
  useEffect(() => {
    if (tickers.length === 0) return;
    const url = `${HOST}/api/news?tickers=${tickers.join(",")}&size=${20}`;
    axios.get(url
    ).then(response => {
      setNewsList(response.data);
    }).catch(err => {
      console.warn(err);
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

    // only add new ticker to the list
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
    <Box className="container-fluid d-flex flex-column" sx={{ minHeight: "100vh" }}>
      {/* header */}
      <Box className="row shadow-sm mb-4 bg-light" sx={{ width: "100vw", borderBottom: "2px solid #4682B4" }}>
        <Header />
      </Box>

      <Box className="row mb-4">
        <Box className="col-12 col-lg-10 col-xxl-8 m-auto">
          <Tabs value={selectTab} onChange={(event, newTab) => setSelectTab(newTab)}>
            <Tab label="Top Gainers" id="tab-0" />
            <Tab label="Top Losers" id="tab-1" />
          </Tabs>
          <CustomTabPanel value={selectTab} index={0}>
            {topGainers.length > 0 && <Carousel items={topGainers} />}
          </CustomTabPanel>
          <CustomTabPanel value={selectTab} index={1}>
            {topLosers.length > 0 && <Carousel items={topLosers} />}
          </CustomTabPanel>
        </Box>
      </Box>
      <Box className="row mb-1 justify-content-center align-items-start">
        <TopBar
          tickers={tickers}
          startDate={userInputs.startDate}
          endDate={userInputs.endDate}
          budget={userInputs.budget}
          ticker={userInputs.ticker}
          onChange={handleUserInputs}
        />
      </Box>

      <Box className="row flex-grow-1">
        <Box className="col-12 col-xl-10 col-xxl-8 m-auto text-center">
          {
            tickers.length === 0 ?
              <Box>
                <img className="img-fluid" src={process.env.PUBLIC_URL + "/no-data.png"} />
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
        <Box className="col-12 col-xl-10 col-xxl-8 m-auto">
          {(tickers.length > 0 && newsList.length > 0) &&
            <>
              <div style={{ width: "100%", color: "black", border: "1px double black", marginTop: "30px" }} />
              <p className="h3 fw-bold my-3">Related News</p>
            </>
          }

          {
            (tickers.length > 0 && newsList.length > 0) &&
            newsList.map((news, idx) =>
              <Box key={news.url}>
                <News
                  title={news.title}
                  url={news.url}
                  imageUrl={news.imageUrl}
                  summary={news.summary}
                  publishedDate={news.publishedDate}
                />
                {idx === newsList.length - 1 || <hr />}
              </Box>
            )
          }
        </Box>
      </Box>

      {/* footer */}
      <Box className="row mt-5" sx={{ width: "100vw", backgroundColor: "#4682B4" }}>
        <Footer />
      </Box>

      {/* snack bar to inform user */}
      {notification.message.length > 0 &&
        <AutoHideSnackBar
          open
          message={notification.message}
          severity={notification.severity}
          onHide={() => setNotification({ ...notification, message: "" })}
          autoHideDuration={notification.autoHideDuration}
        />
      }
    </Box>
  );
}


function CustomTabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`tab-${index}`}
      aria-labelledby={`tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ padding: "10px 0px" }}>
          {children}
        </Box>
      )}
    </div>
  );
}
