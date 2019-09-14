import React from "react";
import { LineChart, ResponsiveContainer, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

import { data } from './utils/data'

// const data = [{name: 'Page A', uv: 400, pv: 2400, amt: 2400}];
function Graph() {

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
  return (
    <div className="graph" style={{width: "80vw", height:"60vh"}}>
      <ResponsiveContainer width='100%' height='100%'>
        {/* <LineChart data={data}>
          <XAxis dataKey="date" angle={-20} textAnchor="end" height={55} />
          <YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
          <CartesianGrid strokeDasharray="3 3" />
          <Tooltip />
          <Legend />
          {lines}
        </LineChart> */}
        <LineChart width={730} height={250} data={data}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="pv" stroke="#8884d8" />
          <Line type="monotone" dataKey="uv" stroke="#82ca9d" />
        </LineChart>
      </ResponsiveContainer>
      {/* <p>here</p> */}
    </div>
  );
}

export default Graph;