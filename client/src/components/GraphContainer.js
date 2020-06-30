import React from 'react'

//components
import Chart from './Chart'
import TopBar from './TopBar'
import SymbolsList from './SymbolsList'

function GraphContainer() {

  return (
    <table className="homecontainer">
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