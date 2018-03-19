//https://stackoverflow.com/a/24103596
function setCookie(name,value,days) {
	var expires = "";
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + (days*24*60*60*1000));
		expires = "; expires=" + date.toUTCString();
	}
	document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function getCookie(name) {
	var nameEQ = name + "=";
	var cookies = document.cookie.split(';');
	for(var i=0;i < cookies.length;i++) {
		var c = cookies[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) {
			return c.substring(nameEQ.length,c.length);
		}
	}
	return null;
}

function eraseCookie(name) {
	expires = "; expires=Thu, 01 Jan 1970 00:00:00 UTC";
	document.cookie = name + "=" + "" + expires + "; path=/";
}

// https://developers.google.com/identity/sign-in/web/
function onSignIn() 
{
	var googleAuth = gapi.auth2.getAuthInstance();

	if ( googleAuth.isSignedIn.get() )
	{
		var googleUser = googleAuth.currentUser.get();
		// The ID token you need to pass to your backend:
		var idToken = googleUser.getAuthResponse().id_token;
		// Pass user data to the server w/ a cookie: http://www.javascripter.net/faq/passingp.htm
		setCookie('currentToken', idToken, 1);
		window.location.replace("http://" + window.location.host + "/home/");
		
		//https://developers.google.com/web/ilt/pwa/working-with-the-fetch-api#example_post_requests
		/*fetch("http://" + window.location.host + '/signin/',{
			method: 'POST',
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			body:'idtoken='+idToken
		});*/
	}
}

