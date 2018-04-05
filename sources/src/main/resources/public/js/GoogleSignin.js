// https://developers.google.com/identity/sign-in/web/
function onSignIn() {
	var googleAuth = gapi.auth2.getAuthInstance();
	if ( googleAuth.isSignedIn.get()) {
		var googleUser = googleAuth.currentUser.get();
		// The ID token passed to your back-end:
		var idToken = googleUser.getAuthResponse().id_token;
		//https://developers.google.com/web/ilt/pwa/working-with-the-fetch-api#example_post_requests
		fetch(window.location.origin + '/googlesignin/', {
			method: 'POST',
			credentials:'include',
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			body:'idtoken='+idToken
		}).then( function(response){
			//https://developers.google.com/web/updates/2015/03/introduction-to-fetch#response_types
			if (response.status >= 200 && response.status < 300){
				window.location.replace( window.location.origin + "/home/");
			}
		});
	}
}

function onSignOut() {
	var googleAuth = gapi.auth2.getAuthInstance();
	if ( googleAuth.isSignedIn.get()){
		googleAuth.signOut();
	}
}

