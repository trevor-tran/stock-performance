import React, { useState } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import update from 'immutability-helper';
import moment from 'moment';

function Home() {
  const [budget, setBudget] = useState("");
  const [start_date, setStartDate ] = useState("");
  const [end_date, setEndDate] = useState("");
  const [symbols, setSymbols] = useState([]);
  const [deleted_symbol, setDeletedSymbol] = useState("");
}