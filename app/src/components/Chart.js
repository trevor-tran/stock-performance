import dayjs from 'dayjs';
import { useState } from 'react';
import { LineChart, ResponsiveContainer, Line, XAxis, YAxis, Tooltip, Legend } from 'recharts';

import "../assets/css/Chart.css";

const COLORS = ['#8884d8', '#82ca9d', '#e57cf9', '#8b2412', '#f83581', '#f07b50', '#0c5e59', '#0011ff', '#595163'];


export default function Chart(props) {
  const [emphasize, setEmphasize] = useState(null);

  const data = [];
  const { stockData, budget } = props;

  let prevDate;

  stockData.forEach((arr, date, thisMap) => {
    let dataPoint = new Object();

    dataPoint["date"] = dayjs(date).format("MMM. YYYY").toString();

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

  return (
    <ResponsiveContainer width='100%' height='100%'>
      <LineChart width={730} height={250} data={data}
        margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
        <XAxis dataKey="date" type="category" allowDuplicatedCategory={false} angle={-20} textAnchor="end" height={55} />
        <YAxis tickFormatter={tick => tick.toLocaleString()} label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
        <Tooltip formatter={(value) => new Intl.NumberFormat('en').format(value)} />
        <Legend verticalAlign="top"
          onClick={e => props.onLegendClick(e.dataKey)}
          onMouseEnter={e => setEmphasize(e.dataKey)}
          onMouseLeave={() => setEmphasize(null)}
        />
        <Legend />
        {data.length > 0 && Object.keys(data[0]).map((k, idx) => {
          if (k !== "date") {
            return <Line key={k} dataKey={k} stroke={COLORS[idx % COLORS.length]} strokeOpacity={!emphasize ? 1 : (emphasize === k ? 1 : 0.3)} dot={false} />
          }
        })}
      </LineChart>
    </ResponsiveContainer>
  );
}
