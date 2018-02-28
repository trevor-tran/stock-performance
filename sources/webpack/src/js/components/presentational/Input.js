import React from "react";
import PropTypes from "prop-types";

const Input = ({ type, id, value, placeholder, handleChange }) => (
    <input
	  	type={type}
	   	id={id}
    	value = {value}
	   	placeholder={placeholder}
	   	onChange={handleChange}
	  	required
	/>
);

Input.propTypes = {
  type: PropTypes.string.isRequired,
  id: PropTypes.string.isRequired
};

export default Input;