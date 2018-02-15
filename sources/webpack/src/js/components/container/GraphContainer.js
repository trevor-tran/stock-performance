import React, { Component } from "react";
import ReactDOM from "react-dom";
//import Input from "../presentational/Input";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';

const data1 = [
	{date: '10/20/1017', AAPL: 400, GOOGL:150},
	{date: '10/10/2018', AAPL: 200, GOOGL:560},
	{date: '10/20/2018', AAPL: 300, GOOGL:160},
	];
const rawPrice = {
          "2018-01-29" : {
                      "GOOGL" : 1186.48,
                          "AAPL" : 167.96
                                    },
            "2018-01-30" : {
                        "GOOGL" : 1177.37,
                            "AAPL" : 166.97
                                      },
              "2018-01-31" : {
                          "GOOGL" : 1182.22,
                              "AAPL" : 167.43
                                        },
                "2018-02-02" : {
                            "GOOGL" : 1119.2,
                                "AAPL" : 160.37
                                          },
                  "2018-02-01" : {
                              "GOOGL" : 1181.59,
                                  "AAPL" : 167.78
                                            }
};
/*
//convert to Map
const map = new Map();
Object.keys(rawPrice).forEach(key => {
            map.set(key, rawPrice[key]);
});
const data2 = [];
map.forEach(function(price,date)
        {
            let entry = new Object();
            entry["date"]=date;
			Object.keys(price).forEach(symbol => {
                entry[symbol] = price[symbol];
			});
			data2.push(entry);
           
		});
*/
class GraphContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				data: false
		};
	}
	
	componentDidMount() {
		var _self = this;
		fetch('http://localhost:4567/stockdata/?user=phuong', { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then(function(rawData) {
			// save JSON stock data to render later
			console.log(rawData);
			//map where key is date, and value is {ticker:price}
			const map = new Map();
			Object.keys(rawData).forEach(key => {
			            map.set(key, rawData[key]);
			});
			//array of objects
			const data = [];
			map.forEach(function(price,date)
			        {
			            let entry = new Object();
			            entry["date"]= date;
						Object.keys(price).forEach(symbol => {
			                entry[symbol] = price[symbol];
						});
						data.push(entry);
			           
					});

			_self.setState({data});
		})
		.catch(function(error) {
			console.log(error);
		});
	}
	render() {
		return (
				<LineChart width={800} height={400} data={this.state.data}
				margin={{top: 5, right: 30, left: 20, bottom: 5}}>
				<XAxis dataKey="date"/>
				<YAxis/>
				<CartesianGrid strokeDasharray="3 3"/>
					<Tooltip/>
				<Legend />
				<Line type="monotone" dataKey="AAPL" dot={false} stroke="#8884d8" />
				<Line type="monotone" dataKey="GOOGL" dot={false} stroke="#82449d" />
				<Line type="monotone" dataKey="MSFT" dot={false} stroke="#00ff00" />		
					</LineChart>
		);
	}
}

const wrapper = document.getElementById("graph");
wrapper ? ReactDOM.render(<GraphContainer />, wrapper) : false;

export default GraphContainer;
