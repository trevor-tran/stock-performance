import dayjs from "dayjs";

const lastDayOfLastMonth = dayjs(Date.now()).subtract(1, "month").endOf("month");
const lastDayOfLastYear = lastDayOfLastMonth.subtract(1, "year");

const formatDate = (date) => {
  return dayjs(date).format("YYYY-MM-DD");
}

export const endOfLastMonth = formatDate(lastDayOfLastMonth);
export const endOfLastYear = formatDate(lastDayOfLastYear);