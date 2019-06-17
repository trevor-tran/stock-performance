//no mark
function noMark(elementId){
	document.getElementById(elementId).innerHTML = "";
}

//valid mark
function validMark(elementId){
	document.getElementById(elementId).innerHTML = "&#10004;";
	document.getElementById(elementId).style.color = "green";
}
//invalid mark
function invalidMark(elementId){
	document.getElementById(elementId).innerHTML = '&#10006;';
	document.getElementById(elementId).style.color = "red";
}

//show a mark indicating whether the password is valid or not
function validatePassword(){
	var password = document.getElementById("password");
	var regex = new RegExp('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$');
	if(password.value===null || password.value===""){
		noMark('passwordmark');
	}else if (regex.test(password.value)) {
		validMark('passwordmark');
	}else {
		invalidMark('passwordmark');
	}	
}

//show a mark indicating whether passwords match or not.
function passwordMatch() {
	var passwordValue = document.getElementById("password").value;
	var reenterPasswordValue = document.getElementById("reenterpassword").value;
	if(reenterpassword.value===null || reenterpassword.value===""){
		noMark('matchmark');
	}else if(passwordValue === reenterPasswordValue) {
		validMark('matchmark');
	}else {
		invalidMark('matchmark');
	}	
}

//show mark indicating whether the mail is valid or not
function validateEmail() {
	var regex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	//var regex = new RegExp('^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$');
	var emailAddress = document.getElementById("emailaddress");
	if(emailAddress.value===null || emailAddress.value===""){
		noMark('emailmark');
	}else if(regex.test(String(emailAddress.value).toLowerCase())) {
		validMark('emailmark');
	}else {
		invalidMark('emailmark');
	}	
}