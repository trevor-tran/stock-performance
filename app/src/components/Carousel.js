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


import "../assets/css/Carousel.css";

export default function Carousel(props) {
  const { items } = props;

  return (
    <>
      <Swiper
        centeredSlides={true}
        loop={true}
        // slidesPerGroup={1}
        breakpoints={{
          640: {
            slidesPerView: 1,
          },
          768: {
            slidesPerView: 2,
          },
          992: {
            slidesPerView: 3,
          },
          1200: {
            slidesPerView: 4,
          },
          1400: {
            slidesPerView: 5,
          },
          1600: {
            slidesPerView: 6,
          },
          1900: {
            slidesPerView: 7,
          },
        }}
        autoplay={{
          delay: 2500,
          disableOnInteraction: false,
        }}
        navigation={true}
        modules={[Autoplay, Navigation]}>
        {
          items.length > 0 && items.map((item, index) =>
            <SwiperSlide key={index}>
              <CarouselItem
                ticker={item.ticker}
                price={item.price}
                changeAmount={item.change_amount}
                changePercentage={item.change_percentage}
              />
            </SwiperSlide>
          )
        }
      </Swiper>
    </>
  )
}

function CarouselItem({ ticker, price, changeAmount, changePercentage }) {
  const sign = Number(changeAmount) > 0 ? "+" : "";

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
