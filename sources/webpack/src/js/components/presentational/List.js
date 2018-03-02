import React, { Component } from "react";

class List extends Component {
	constructor(props){
		super(props);
		this.removeHandler = this.removeHandler.bind(this);
	}

	removeHandler(e){
		//must work around since there is nothing attribute like "value"
		this.props.handleDelete(e.target.parentNode.firstChild.innerText);
	}

	render() {
		return (

				<table className="symbolscontainer">
				<tbody>
				{this.props.symbols.map( (symbol)=>(
						<tr key={symbol}>
							<td>{symbol}</td>
							<td id="delete" title="delete?" onClick={this.removeHandler}>&#10006;</td>
						</tr>	
				))}
				</tbody>
				</table>
				);
	}
}

export default List;