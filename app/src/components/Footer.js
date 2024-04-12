import { Box, IconButton } from '@mui/material';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import GitHubIcon from '@mui/icons-material/GitHub';
import EmailIcon from '@mui/icons-material/Email';
import FavoriteIcon from '@mui/icons-material/Favorite';

import "../assets/css/Footer.css";

export default function Footer() {
  return (
    <footer style={{color: "white"}} className="d-lg-flex m-auto py-4 justify-content-center align-items-center">
      <Box className="col-12 col-lg-7 col-xxl-6 me-lg-5" sx={{textAlign: "justify"}}>
        <span><span className="text-uppercase fw-bold">disclaimer: </span>The information provided by this tool is for illustrative purposes only and is not intended to represent actual investment outcomes.
          This tool should not be used as the sole basis for any investment decisions.
          By using this site, you agree not to hold us liable for any decisions or outcomes based on its information.</span>
      </Box>
      <Box className="col-12 col-lg-3 col-xxl-2 text-center mt-5 m-lg-0">
        <a  href="https://trevortran.com" target="_blank" rel="noreferrer">
          <span className="mb-3 mb-md-0 ps-1">© 2024 Trevor Tran</span>
        </a>
        <span className="d-block"> Made with <FavoriteIcon className="heartbeat" sx={{ color: "red" }} /> from Seattle</span>
      </Box>
    </footer>
  );
}
