let utils = {
  removeSymbol: (symbol) => {
    fetch(window.location.origin + '/removesymbol/', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: 'symbol=' + symbol
    }).then(function (response) {
      //https://developers.google.com/web/updates/2015/03/introduction-to-fetch#response_types
      if (response.status > 300) {
        return false;
        // alert("There is an error. Please sign out, close browser, and try again.");
      }
      return true;
    });
  }
}

export default utils;