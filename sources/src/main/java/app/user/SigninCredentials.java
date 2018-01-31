package app.user;
import lombok.*;
@Value
public class SigninCredentials {

		private String salt;
		private String hashedPassword;
}
