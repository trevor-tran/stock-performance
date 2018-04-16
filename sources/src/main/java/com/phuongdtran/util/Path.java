package com.phuongdtran.util;

import lombok.Getter;

public class Path {

	public static class Web {
		@Getter public static final String HOME = "/home/";
		@Getter public static final String SIGNIN = "/signin/";
		@Getter public static final String SIGNUP = "/signup/";
		@Getter public static final String SIGNOUT = "/signout/";
		@Getter public static final String SUMMARY = "/summary/";
		@Getter public static final String GOOGLESIGNIN = "/googlesignin/";
		@Getter public static final String UPDATE = "/update/";
		@Getter public static final String REMOVESYMBOL = "/removesymbol/";
		@Getter public static final String STOCKDATA = "/stockdata/";
	}
	
	public static class Templates {
		public static final String HOME = "/velocity/home.vm";
		public static final String SIGNIN = "/velocity/signin.vm";
		public static final String SIGNUP = "/velocity/signup.vm";
		public static final String NOT_FOUND = "/velocity/notFound.vm";
	}
}
