package com.phuongdtran.user;
import org.mindrot.jbcrypt.BCrypt;

public final class Password {

		private final String salt;
		private final String hash;
		
		public Password(String salt, String hash){
			this.salt = salt;
			this.hash = hash;
		}
		
		public Password( String plainText){
			this.salt = BCrypt.gensalt();
			this.hash = BCrypt.hashpw(plainText, salt);
		}

		public boolean matches(String passphrase){
			String hashedText = BCrypt.hashpw(passphrase, this.salt);
			return this.hash.equals(hashedText);
		}

		public String getHash() {
			return hash;
		}

		public String getSalt() {
			return salt;
		}
}
