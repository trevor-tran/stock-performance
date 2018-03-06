package app.user;
import lombok.*;
@Value
public class Password {

		private String salt;
		private String hashedPassword;
}
