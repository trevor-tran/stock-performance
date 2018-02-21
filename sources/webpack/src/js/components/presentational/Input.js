import React from "react";
import PropTypes from "prop-types";

const Input = ({ type, id, placeholder, handleChange }) => (
    <input
	  	type={type}
	   	id={id}
	   	placeholder={placeholder}
	   	onChange={handleChange}
	  	required
	/>
);

Input.propTypes = {
  type: PropTypes.string.isRequired,
  id: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  placeholder: PropTypes.string.isRequired,
  handleChange: PropTypes.func.isRequired
};

export default Input;