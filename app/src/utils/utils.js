
const DEV_HOST = "http://localhost:8181";
const PROD_HOST = "https://trevortran.com/stockcomparator/server";
export const HOST =  PROD_HOST;

export const MAXIMUM_TICKERS = 3;

// stale time in hours
export const STALE_TIME = {
  stock: 12,
  gainersLosers: 24,
  news: 5,
}

export const NUMBER_OF_NEWS_ARTICLES = 20;