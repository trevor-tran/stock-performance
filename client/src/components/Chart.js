import React, { useEffect, useContext, useState } from "react";
import { LineChart, ResponsiveContainer, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

import { Paper } from "@material-ui/core";

import { Context } from '../store';
import { sessions, fetchURLs } from './utils/Constants';
import moment from 'moment';

import Overlay from "./Overlay";

const COLORS = ['#8884d8', '#82ca9d', '#e57cf9', '#8b2412', '#f83581', '#f07b50', '#0c5e59', '#0011ff', '#595163'];

function Chart() {

  let stockData = [];
  if (sessionStorage.getItem(sessions.STOCK_DATA)) {
    stockData = JSON.parse(sessionStorage.getItem(sessions.STOCK_DATA));
  }

  const { state } = useContext(Context);
  const [parsedData, setParsedData] = useState(stockData);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    // there are TWO actions changing state.symbols
    // a symbol is either added to the list or removed from the list
    const { symbols, end_date, start_date, budget } = state;
    if (!Array.isArray(symbols) || symbols.length === 0) {
      setParsedData([]);
      sessionStorage.setItem(sessions.STOCK_DATA, []);
    } else if (parsedData.length > state.symbols.length) {
      // a symbol removed from state.symbols, therefore corresponding stock data in parsedData also need removed
      // no API calls needed
      const parsedDataAfterSymbolRemoved = [];
      parsedData.forEach(obj => {
        if (symbols.includes(obj.symbol)) {
          const copy = { ...obj };
          parsedDataAfterSymbolRemoved.push(copy);
        }
      });
      setParsedData(parsedDataAfterSymbolRemoved);
      sessionStorage.setItem(sessions.STOCK_DATA, JSON.stringify(parsedDataAfterSymbolRemoved));
    } else {
      setIsLoading(true);
      // need to get stock data from server
      const url = fetchURLs.DATA;
      fetch(url, {
        method: 'POST',
        body: JSON.stringify({
          symbols: symbols,
          start_date: start_date,
          end_date: end_date
        }),
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(response => {
        return response.json();

      }).then(json => {
        if (json.success) {
          const balances = computeBalances(budget, dataParser(symbols, JSON.parse(json.msg)));
          setParsedData(balances);
          setIsLoading(false);
          // session storage
          sessionStorage.setItem(sessions.STOCK_DATA, JSON.stringify(balances));
        } else {
          throw json.msg;
        }
      }).catch(err => {
        setIsLoading(false);
        console.error(err);
      });
    }
    // session storage
    sessionStorage.setItem(sessions.USER_STATE, JSON.stringify(state));
  }, [state.symbols, state.start_date, state.end_date, state.budget]);

  const computeBalances = (budget, parsedData) => {
    const balances = parsedData.map(obj => {
      let { data } = obj;
      let numShares = 1;
      const newData = data.map((subObj, index) => {
        let newSubObj = { ...subObj };
        if (index === 0) {
          numShares = Number(budget) * 1.0 / Number(subObj.value);
          newSubObj.value = Number(budget);
        } else {
          const {split, dividend, value} = subObj; // "value" is price
          // assumption: stock split and dividend don't occure on same day
          numShares = numShares * Number(split);
          numShares += numShares * Number(dividend) / Number(value);
          newSubObj.value = roundUp(numShares * Number(value), 2); // "newSubObj.value" is balance
        }
        return newSubObj;
      });
      let newObj = { ...obj };
      newObj.data = newData;
      return newObj;
    });
    return balances;
  }

  // data fetched from server is in different format from what expected to use in Rechart
  // reference utils/data.js to see sample data used in Rechart.
  // this function parses server-format data to Rechart-format data
  // @params:
  //      json: json data got from server
  //      list of symbols looked up in json
  const dataParser = (symbols, json) => {
    let parsedData = [];

    symbols.forEach(symbol => {
      const data = json[symbol];
      let stockInfoForASymbol = {};
      stockInfoForASymbol["symbol"] = symbol;
      stockInfoForASymbol["data"] = [];
      data.map(oneday =>
        stockInfoForASymbol["data"].push({
          date: oneday.date,
          value: Number(oneday.price),
          split: Number(oneday.split),
          dividend: Number(oneday.dividend)
        })
      );

      // since date is in YYYY-MM-DD format, sort it before changing to user-friendly date format
      stockInfoForASymbol["data"].sort((o1, o2) => o1.date.localeCompare(o2.date));

      // change to user-friendly date format
      stockInfoForASymbol["data"].forEach(obj => {
        obj.date = moment(obj.date, "YYYY-MM-DD", true).format("DD MMM. YYYY");
      });
      parsedData.push(stockInfoForASymbol);
    })
    return parsedData;
  }

  // https://stackoverflow.com/questions/5191088/how-to-round-up-a-number-in-javascript
  function roundUp(num, precision) {
    precision = Math.pow(10, precision)
    return Math.ceil(num * precision) / precision
  }

  return (
    <React.Fragment>
      <Paper className="graph" style={{ width: "80vw", height: "60vh", margin: 'auto' }}>
        <ResponsiveContainer width='100%' height='100%'>
          <LineChart width={730} height={250} data={parsedData}
            margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" type="category" allowDuplicatedCategory={false} angle={-20} textAnchor="end" height={55} />
            <YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
            <Tooltip />
            <Legend />
            <Legend />
            {parsedData.map((s, idx) => (
              <Line dataKey="value" data={s.data} name={s.symbol} key={s.symbol} stroke={COLORS[idx % COLORS.length]} dot={false} />
            ))}
          </LineChart>
        </ResponsiveContainer>
      </Paper>
      {isLoading ? <Overlay /> : null}
      {/* <Overlay/> */}
    </React.Fragment>
  );
}

export default Chart;