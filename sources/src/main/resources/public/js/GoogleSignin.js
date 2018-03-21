// https://developers.google.com/identity/sign-in/web/
function onSignIn() {
	var googleAuth = gapi.auth2.getAuthInstance();
	if ( googleAuth.isSignedIn.get()) {
		var googleUser = googleAuth.currentUser.get();
		// The ID token you need to pass to your backend:
		var idToken = googleUser.getAuthResponse().id_token;
		window.location.replace("http://" + window.location.host + "/home/");
		
		//https://developers.google.com/web/ilt/pwa/working-with-the-fetch-api#example_post_requests
		fetch("http://" + window.location.host + '/googlesignin/', {
			method: 'POST',
			credentials:'include',
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			body:'idtoken='+idToken
		});
	}
}

function onSignOut() {
	var googleAuth = gapi.auth2.getAuthInstance();
	if ( googleAuth.isSignedIn.get()){
		googleAuth.signOut();
	}
}

