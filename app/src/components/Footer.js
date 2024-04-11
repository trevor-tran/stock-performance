import { Box, IconButton } from '@mui/material';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import GitHubIcon from '@mui/icons-material/GitHub';
import EmailIcon from '@mui/icons-material/Email';
import FavoriteIcon from '@mui/icons-material/Favorite';

import "../assets/css/Footer.css";

export default function Footer() {
  return (
    <Box className="col-12 col-lg-10 col-xxl-8 m-auto">
      <footer className="d-flex flex-wrap justify-content-between align-items-center">
        <Box className="col-md-4 d-flex align-items-center">
          <a className="text-body-secondary" href="https://trevortran.com" target="_blank" rel="noreferrer">
            <span className="mb-3 mb-md-0 ps-1 text-body-secondary">© 2024 Trevor Tran</span>
          </a>
        </Box>
        <Box>
          <span className="text-body-secondary"> Made with <FavoriteIcon className="heartbeat" sx={{color: "red"}}/> from Seattle</span>
        </Box>
      </footer>
    </Box>
  );
}
