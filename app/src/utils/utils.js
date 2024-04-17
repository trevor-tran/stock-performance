
const DEV_HOST = "http://localhost:8080";
const PROD_HOST = "https://trevortran.com:8080";
export const HOST = process.env.NODE_ENV === 'prod' ? PROD_HOST : DEV_HOST;