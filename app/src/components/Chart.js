import dayjs from 'dayjs';
import { useState } from 'react';
import { LineChart, ResponsiveContainer, Line, XAxis, YAxis, Tooltip, Legend, CartesianGrid } from 'recharts';

import { Button, Tooltip as MuiTooltip } from '@mui/material';

const COLORS = ['#8884d8', '#82ca9d', '#e57cf9', '#8b2412', '#f83581', '#f07b50', '#0c5e59', '#0011ff', '#595163'];



const renderCustomizedLegend = props => {
  const { payload } = props;

  function handleOnClick(e) {
    e.preventDefault();
    props.onClick(payload[e.currentTarget.value]);
    props.onMouseLeave();
  }

  function handleOnMouseEnter(e) {
    e.preventDefault();
    props.onMouseEnter(payload[e.currentTarget.value]);

  }

  function handleOnMouseLeave(e) {
    e.preventDefault();
    props.onMouseLeave();
  }

  return (
    <div>
      {
        payload.map((entry, index) => (
          <MuiTooltip key={`item-${index}`} title="Click to delete" arrow placement="bottom">
            <Button sx={{ padding: 0, marginRight: "15px" }} variant="text" value={index}
              onMouseEnter={handleOnMouseEnter}
              onMouseLeave={handleOnMouseLeave}
              onClick={handleOnClick}>
              <span style={{ display: "inline-block", width: "20px", height: "10px", backgroundColor: entry.color, marginRight: "10px" }}></span>
              <span className="fs-6 fw-bold" style={{ color: entry.color }}>{entry.value}</span>
            </Button>
          </MuiTooltip>
        ))
      }
      <p className="small text-secondary"><strong>footnote:</strong> click on a legend to delete it</p>
    </div>
  )
}



export default function Chart(props) {
  const [emphasize, setEmphasize] = useState(null);

  const data = [];
  const { stockData, budget } = props;

  let prevDate;

  stockData.forEach((arr, date, thisMap) => {
    let dataPoint = {};

    // console.log(date)

    dataPoint["date"] = dayjs(date).format("MMM 'YY").toString();

    // the arr is an array datatype that contains tickers and its price
    for (let index = 0; index < arr.length; index++) {
      const { ticker, endOfMonthPrice } = arr[index];

      // if it's the day when money was invested,
      // no need to do any calculations.
      // Otherwise, compute the performance of current month based on previous monthh
      if (!prevDate) {
        dataPoint[ticker] = budget;
      } else {
        // current performance is the ratio of current month price over previous month price
        const prevMonthPrice = thisMap.get(prevDate)[index].endOfMonthPrice;
        const returnRate = Number(endOfMonthPrice) / Number(prevMonthPrice);

        // calculate current balance based off of previous balance and current performance rate
        const prevBalance = data[data.length - 1][ticker];
        const currentBalance = Number(prevBalance) * returnRate;

        dataPoint[ticker] = currentBalance;
      }
    }

    data.push(dataPoint);
    prevDate = date;
  });

  function formatUSD(value, fractionDigits = 0) {
    return new Intl.NumberFormat('en', {
      style: 'currency',
      currency: 'USD',
      notation: "compact",
      compactDisplay: "short",
      minimumFractionDigits: fractionDigits,
      maximumFractionDigits: fractionDigits
    }).format(value);
  }

  return (
    <ResponsiveContainer width='100%' height='100%'>
      <LineChart data={data}
        margin={{ top: 10, bottom: 10, right: 5, left: 5 }}>
        <XAxis dataKey="date" type="category" allowDuplicatedCategory={false} angle={-20} textAnchor="end" height={55} />
        <YAxis tickFormatter={value => formatUSD(value)} />
        <Tooltip formatter={(value) => formatUSD(value, 2)} />
        <Legend
          onClick={e => props.onLegendClick(e.dataKey)}
          onTouchEnd={e => props.onLegendClick(e.dataKey)}
          onMouseEnter={e => setEmphasize(e.dataKey)}
          onMouseLeave={() => setEmphasize(null)}
          content={renderCustomizedLegend}
        />
        {data.length > 0 && Object.keys(data[0]).map((k, idx) => {
          if (k !== "date") {
            return <Line key={k} dataKey={k}
              strokeWidth={!emphasize ? 3 : (emphasize === k ? 3 : 1)}
              stroke={COLORS[idx % COLORS.length]}
              strokeOpacity={!emphasize ? 1 : (emphasize === k ? 1 : 0.3)}
              dot={false} />
          }
        })}
         <CartesianGrid strokeDasharray="5 5"/>
      </LineChart>
    </ResponsiveContainer>
  );
}
