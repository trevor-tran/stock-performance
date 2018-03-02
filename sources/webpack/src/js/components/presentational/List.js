import React, { Component } from "react";

class List extends Component {
	constructor(props){
		super(props);
		this.removeHandler = this.removeHandler.bind(this);
	}

	removeHandler(e){
		this.props.handleDelete(e.target.value);
	}

	render() {
		return (

				<table className="symbolscontainer">
				<tbody>
				{this.props.symbols.map( (symbol)=>(
						<tr value={symbol} key={symbol} onClick={this.removeHandler}>
							<td>{symbol}</td>
							<td id="delete" title="delete?" >&#10006;</td>
						</tr>	
				))}
				</tbody>
				</table>
				);
	}
}

export default List;