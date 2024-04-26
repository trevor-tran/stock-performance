import { Box, IconButton, Tooltip, } from '@mui/material';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import GitHubIcon from '@mui/icons-material/GitHub';
import EmailIcon from '@mui/icons-material/Email';

import "../assets/css/Header.css";

export default function Header(props) {
  return (
    <Box className="col-12 col-xl-10 col-xxl-8 m-auto">
      <header className="d-flex flex-column flex-sm-row flex-wrap justify-content-center justify-content-sm-between align-items-center">
        <a href="/" className="d-flex align-items-center text-decoration-none">
          <span className="fs-2 fw-bold">Stock Comparator <sup className="fs-6">beta</sup></span>
        </a>
        <Box className="d-flex flex-column flex-sm-row justify-content-between align-items-center">
          <Tooltip title="Consider donating to support our work">
            <a
              className="donation-button btn me-0 me-sm-2 me-md-4 order-1 order-sm-0"
              style={{ textDecoration: "none", height: "45px" }}
              href="https://www.paypal.com/donate/?business=MXF7G339EFAJ2&no_recurring=0&item_name=Please+consider+supporting+our+efforts+with+a+donation.+Your+contribution+will+help+us+cover+the+costs+of+maintaining+the+app.&currency_code=USD"
              role="button"
              target="_blank" rel="noreferrer">
              <img src={process.env.PUBLIC_URL + '/donation.png'} alt="PayPal" />
              <span className="d-none d-md-inline" style={{ color: "white", fontWeight: "bold", fontSize: "20px", marginLeft: "7px" }}>Donate</span>
            </a>
          </Tooltip>
          <div class="d-none d-sm-block ms-md-1 vr" style={{ height: "35px", verticalAlign: "center", margin: "auto" }}></div>
          <ul className="nav list-unstyled ms-0 ms-sm-2 ms-md-3">
            <li className="ms-0">
              <Tooltip title="LinkedIn">
                <a href="https://www.linkedin.com/in/trevorpt/" target="_blank" rel="noreferrer">
                  <IconButton aria-label="linkedin">
                    <LinkedInIcon fontSize="large" />
                  </IconButton>
                </a>
              </Tooltip>
            </li>
            <li className="ms-0 ms-md-1">
              <Tooltip title="GitHub">
                <a href="https://github.com/trevor-tran" target="_blank" rel="noreferrer">
                  <IconButton aria-label="github">
                    <GitHubIcon fontSize="large" />
                  </IconButton>
                </a>
              </Tooltip>
            </li>
            <li className="ms-0 ms-md-1">
              <Tooltip title="Email">
                <a href="mailto:trevor.phuong.tran@gmail.com">
                  <IconButton aria-label="github">
                    <EmailIcon fontSize="large" />
                  </IconButton>
                </a>
              </Tooltip>
            </li>
          </ul>
        </Box>
      </header>
    </Box>
  );
}