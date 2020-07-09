import React from 'react'
import CircularProgress from '@material-ui/core/CircularProgress';
import "./css/Overlay.css"

function Overlay() {
  return (
    <div className="overlay-div">
      <CircularProgress/>
    </div>
  )
}

export default Overlay;