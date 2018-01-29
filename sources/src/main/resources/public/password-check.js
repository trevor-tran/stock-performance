function validatePassword(){
		var password = document.getElementById("password");
		var regex = new RegExp('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$');
		if(password.value==null || password.value==""){
			document.getElementById("validationmark").innerHTML = "";
		}
		else if (regex.test(password.value)) {
			document.getElementById("validationmark").innerHTML = "&#10004;";
			document.getElementById("validationmark").style.color = "green";
		}
		else {
			document.getElementById("validationmark").innerHTML = '&#10006;';
			document.getElementById("validationmark").style.color = "red";
		}	
	}
function passwordMatch() {
	var passwordValue = document.getElementById("password").value;
	var reenterPasswordValue = document.getElementById("reenterpassword").value;
	if(reenterpassword.value==null || reenterpassword.value==""){
		document.getElementById("matchmark").innerHTML = "";
	}
	else if(passwordValue == reenterPasswordValue) {
		document.getElementById("matchmark").innerHTML = "&#10004;";
		document.getElementById("matchmark").style.color = "green";
	}
	else {
		document.getElementById("matchmark").innerHTML = '&#10006;';
		document.getElementById("matchmark").style.color = "red";
	}	
}