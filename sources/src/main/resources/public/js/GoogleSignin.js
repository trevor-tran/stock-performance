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
		// Useful data for your client-side scripts:
		var profile = googleUser.getBasicProfile();
		console.log("ID: " + profile.getId()); // Don't send this directly to your server!
		console.log('Full Name: ' + profile.getName());
		console.log('Given Name: ' + profile.getGivenName());
		console.log('Family Name: ' + profile.getFamilyName());
		console.log("Image URL: " + profile.getImageUrl());
		console.log("Email: " + profile.getEmail());

		// The ID token you need to pass to your backend:
		var id_token = googleUser.getAuthResponse().id_token;
		console.log("ID Token: " + id_token);

		// Pass user data to the server w/ a cookie: http://www.javascripter.net/faq/passingp.htm
		setCookie('currentToken', id_token, 1);	        
		//setCookie('currentUser', profile.getEmail(), 1);
		// This did not work:
		//window.location.replace("http://localhost:4567/books?currentUser=phuong");
		// Redirect after sign in to the books page
		window.location.replace( window.location.host + "/home/");
	}
}

