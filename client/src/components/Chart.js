import React, { useEffect, useContext, useState } from "react";
import { LineChart, ResponsiveContainer, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

import { data, series } from './utils/data';
import { Paper } from "@material-ui/core";

import { Context } from '../store';
import { urls } from './utils/Constants';
import moment from 'moment';

function Chart() {

  const { state } = useContext(Context);

  const [parsedData, setParsedData] = useState([]);

  // //properties
  // const data = this.props.getData;
  // const name = this.props.setClassName;
  // const symbols = this.props.getSymbols;

  // var lines = [];
  // var colors = ['#8884d8', '#82ca9d', '#e57cf9', '#8b2412', '#f83581', '#f07b50', '#0c5e59', '#0011ff', '#595163'];
  // var colorIndex = 0;
  // symbols.forEach(function (symbol) {
  //   var color = colors[colorIndex++ % colors.length]; // rotate colors
  //   lines.push(<Line type="monotone" key={symbol} dataKey={symbol} dot={false} unit=" USD" stroke={color} />);
  // });
  useEffect(() => {
    // validate info on server (check if username already exists etc.)

    // there are TWO actions changing state.symbols
    // a symbol is either added to the list or removed from the list
    const {symbols} = state;
    if (!Array.isArray(symbols) || symbols.length == 0) {
      setParsedData([]);
    } else if (parsedData.length > state.symbols.length) {
      // a symbol removed from state.symbols, therefore corresponding stock data in parsedData also need removed
      // no API calls needed
      console.log("a symbol removed")
      const parsedDataAfterSymbolRemoved = [];
      parsedData.forEach(obj => {
        if (symbols.includes(obj.symbol)) {
          const copy = {...obj};
          parsedDataAfterSymbolRemoved.push(copy);
        }
      });
      setParsedData(parsedDataAfterSymbolRemoved);
    } else {
      // need to get stock data from server
      console.log("fetching data from server...");

      let url = urls.SERVER_URL + urls.DATA;
      fetch(url, {
        method: 'POST',
        body: JSON.stringify({
          symbols: symbols
        }),
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(response => {
        return response.json();

      }).then(json => {
        if (json.success) {
          setParsedData(dataParser(symbols, JSON.parse(json.msg)));
        } else {
          throw json.msg;
        }
      }).catch(err => {
        console.error(err);
      });
    }
  }, [state.symbols]);

  // data fetched from server is in different format from what expected to use in Rechart
  // reference utils/data.js to see sample data used in Rechart.
  // this function parses server-format data to Rechart-format data
  // @params:
  //      json: json data got from server
  //      list of symbols looked up in json
  const dataParser = (symbols, json) => {
    let parsedData = [];
    symbols.map(symbol => {
      const data = json[symbol];
      let stockInfoForASymbol = new Object();
      stockInfoForASymbol["symbol"] = symbol;
      stockInfoForASymbol["data"] = new Array();
      data.map(oneday =>
        stockInfoForASymbol["data"].push({ date: oneday.date, value: Number(oneday.price) })
      );

      // since date is in YYYY-MM-DD format, sort it before changing to user-friendly date format
      stockInfoForASymbol["data"].sort((o1, o2) => o1.date.localeCompare(o2.date));

      // change to user-friendly date format
      stockInfoForASymbol["data"].forEach (obj => {
        obj.date = moment(obj.date, "YYYY-MM-DD", true).format("DD MMM. YYYY");
      });
      parsedData.push(stockInfoForASymbol);
    })
    return parsedData;
  }

  return (
    <Paper className="graph" style={{ width: "80vw", height: "60vh", margin: 'auto' }}>
      <ResponsiveContainer width='100%' height='100%'>
        {/* <LineChart data={data}>
          <XAxis dataKey="date" angle={-20} textAnchor="end" height={55} />
          <YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
          <CartesianGrid strokeDasharray="3 3" />
          <Tooltip />
          <Legend />
          {lines}
        </LineChart> */}
        <LineChart width={730} height={250} data={parsedData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          {/* <XAxis dataKey="name" />
          <YAxis /> */}
          <XAxis dataKey="date" type="category" allowDuplicatedCategory={false} angle={-20} textAnchor="end" height={55} />
          <YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
          <Tooltip />
          <Legend />
          <Legend />
          {parsedData.map(s => (
            <Line dataKey="value" data={s.data} name={s.symbol} key={s.symbol} />
          ))}
          {/* <Line type="monotone" dataKey="pv" stroke="#8884d8" />
          <Line type="monotone" dataKey="uv" stroke="#82ca9d" /> */}
        </LineChart>
      </ResponsiveContainer>
      {/* <p>here</p> */}
    </Paper>
  );
}

export default Chart;