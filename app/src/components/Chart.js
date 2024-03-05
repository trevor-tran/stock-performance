import dayjs from 'dayjs';
import { LineChart, ResponsiveContainer, Line, XAxis, YAxis, Tooltip, Legend } from 'recharts';

const COLORS = ['#8884d8', '#82ca9d', '#e57cf9', '#8b2412', '#f83581', '#f07b50', '#0c5e59', '#0011ff', '#595163'];

function Chart(props) {
  const data = [];
  const { stockData, budget } = props;

  let prevDate;
  stockData.forEach((arr, date, thisMap) => {
      let dataPoint = new Object();

      dataPoint["date"] = dayjs(date).format("MMM. YYYY").toString();

      for (let index = 0; index < arr.length; index++) {
        const {ticker, endOfMonthPrice} = arr[index];
        if (!prevDate) {
          dataPoint[ticker] = budget;
        } else {
          const prevMonthPrice = thisMap.get(prevDate)[index].endOfMonthPrice;
          const returnRate = Number(endOfMonthPrice) / Number(prevMonthPrice);
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
        <YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
        <Tooltip />
        <Legend verticalAlign="top"/>
        <Legend />
        {data.length > 0 && Object.keys(data[0]).map((k, idx) => {
          if (k !== "date") {
            return <Line key={k} dataKey={k} stroke={COLORS[idx % COLORS.length]} dot={false} />
          }
        })}
      </LineChart>
    </ResponsiveContainer>
  );
}

export default Chart;