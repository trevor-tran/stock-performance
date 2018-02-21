import React from "react";
import PropTypes from "prop-types";

const Button = ({text,type, id, handleClick }) => (
	<button
	   	type={type}
	   	id={id}
	   	onClick={handleClick} >{text}</button>
);
export default Button;