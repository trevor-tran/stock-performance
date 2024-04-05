// Swiper related imports
// -----start-----
import { Swiper, SwiperSlide } from 'swiper/react';
// Import Swiper styles
import 'swiper/css';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
// import required modules
import {
  Autoplay,
  Navigation
} from 'swiper/modules';
// -----end-----

import {
  Card,
  CardContent
} from '@mui/material';

import "../assets/css/Carousel.css";

export default function Carousel(props) {
  const { items } = props;

  return (
    <>
      <Swiper
        slidesPerView={6}
        centeredSlides={true}
        spaceBetween={10}
        loop={true}
        autoplay={{
          delay: 2500,
          disableOnInteraction: false,
        }}
        pagination={{
          clickable: true,
        }}
        navigation={true}
        modules={[Autoplay, Navigation]}>
        {
          items.map((item, index) =>
            <SwiperSlide key={index}>
              <CarouselItem
                ticker={item.ticker}
                price={item.price}
                changeAmount={item.changeAmount}
                changePercentage={item.changePercentage}
              />
            </SwiperSlide>
          )
        }
      </Swiper>
    </>
  )
}

function CarouselItem({ ticker, price, changeAmount, changePercentage }) {
  const sign = Number(changeAmount) > 0 ? "+" : "-";
  const roundPercentage = Number(changePercentage.replace("%", "")).toFixed(2);
  const change = `${sign}${changeAmount} (${sign}${roundPercentage}%)`;
  return (
    <div className="slide-item" style={{ width: "190px", padding: "20px"}}>
      <span className="d-block h6 text-primary">{ticker}</span>
      <span className="d-block">{price}</span>
      <span className={changeAmount > 0 ? "text-success" : "text-danger"}>{change}</span>
    </div>
  )
}