import React from 'react'

//components
import Chart from './Chart'
import TopBar from './TopBar'
import SymbolsList from './SymbolsList'

function GraphContainer() {
  return (
    <table className="bar-chart-container" style={{margin: "auto", textAlign: "center"}}>
      <tbody>
        <tr>
          <td colSpan="2"><TopBar /></td>
        </tr>
        <tr>
          <td><Chart /></td>
          <td style={{ verticalAlign: "top" }}> <SymbolsList/> </td>
        </tr>
      </tbody>
    </table>
  );
}

export default GraphContainer;