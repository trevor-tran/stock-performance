import React from 'react'

//components
import Chart from './Chart'
import TopBar from './TopBar'

function GraphContainer() {

  return (
    <React.Fragment>
      <TopBar/>
      <Chart/>
    </React.Fragment>
  )
}

export default GraphContainer;