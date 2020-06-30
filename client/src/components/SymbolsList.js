import React, {useContext} from "react";
import { Context } from '../store';
import {types, urls} from './utils/Constants'

function SymbolsList() {

	const {state, dispatch} = useContext(Context);

	const removeSymbol = (e) => {
		dispatch({type: types.REMOVE_SYSMBOL, payload: {symbol: e.target.id}});
	}
	const crossStyle = { color: "red", cursor: "pointer" };
	return (
		<table>
			<tbody>
				{state.symbols.map((symbol) => (
					<tr key={symbol}>
						<td>{symbol}</td>
						<td id={symbol} name={symbol} style={crossStyle} title="delete" onClick={removeSymbol}>&#10006;</td>
					</tr>
				))}
			</tbody>
		</table>
	);
}

export default SymbolsList;