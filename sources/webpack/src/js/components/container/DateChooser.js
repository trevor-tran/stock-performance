import React from 'react';
import ReactDOM from 'react-dom';
import DatePicker from 'react-datepicker';
import moment from 'moment';


//import 'react-datepicker/dist/react-datepicker.css';

class DateChooser extends React.Component {
	constructor (props) {
		super(props);
		this.state = {
			startDate: moment()
			//endDate: moment();
		};
		this.handleChange = this.handleChange.bind(this);
	}
	handleChange(date) {
		this.setState ({startDate: date});
	}
	render() {
		return <DatePicker selected = {this.state.startDate} onChange={this.handleChange} />;
	}
}
const dateWrapper = document.getElementById("startdate");
dateWrapper ? ReactDOM.render(<DateChooser/>, dateWrapper) : false;

export default DateChooser;
