import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';
import PropTypes from 'react';
import async from 'async';

import Summary from "../presentational/Summary";

class SummaryContainer extends Component{
	render(){
		return(
			<Summary setClassName={this.props.setClassName}
			/>
		);
	}
}

export default SummaryContainer;