package com.phuongdtran.user;

import lombok.Getter;

public final class User {
    @Getter private final String username;
    @Getter private final Password password;
    @Getter private final String firstName;
    @Getter private final String lastName;
    @Getter private final String email;

    public User(Builder builder) {
        username = builder.username;
        password = builder.password;
        firstName = builder.firstName;
        lastName = builder.lastName;
        email = builder.email;
    }

    public static class Builder {
        private final String username;
        private final Password password;
        private String firstName;
        private String lastName;
        private String email;

        public Builder(String username, Password password) {
            this.username = username;
            this.password = password;
        }
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
