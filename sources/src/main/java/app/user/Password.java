package app.user;
import org.mindrot.jbcrypt.BCrypt;

import lombok.Value;
@Value
public class Password {

		private String salt;
		private String hashedPassword;
		
		public Password(String salt, String hashedPassword){
			this.salt = salt;
			this.hashedPassword = hashedPassword;
		}
		
		public Password( String plainText){
			this.salt = BCrypt.gensalt();
			this.hashedPassword = BCrypt.hashpw(plainText, salt);
		}
		
		public boolean matches(String plainText){
			String hashedText = BCrypt.hashpw(plainText, this.salt);
			return this.hashedPassword.equals(hashedText);
		}
		
}
