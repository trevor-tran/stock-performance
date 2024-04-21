import { Box, IconButton } from '@mui/material';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import GitHubIcon from '@mui/icons-material/GitHub';
import EmailIcon from '@mui/icons-material/Email';

export default function Header(props) {
  return (
    <Box className="col-12 col-xl-10 col-xxl-8 m-auto">
      <header className="d-flex flex-column flex-sm-row flex-wrap flex-wrap justify-content-center justify-content-sm-between align-items-center">
        <a href="/" className="d-flex align-items-center text-decoration-none">
          <span className="fs-4 fw-bold">Stock Comparator <sup className="fs-6">beta</sup></span>
        </a>
        <ul className="nav ms-sm-auto col-md-4 justify-content-end list-unstyled d-flex">
          <li className="ms-0 ms-md-3 ms-lg-2">
            <a href="https://www.linkedin.com/in/trevorpt/" target="_blank" rel="noreferrer">
              <IconButton aria-label="linkedin">
                <LinkedInIcon />
              </IconButton>
            </a>
          </li>
          <li className="ms-0 ms-md-3 ms-lg-2">
            <a href="https://github.com/trevor-tran" target="_blank" rel="noreferrer">
              <IconButton aria-label="github">
                <GitHubIcon />
              </IconButton>
            </a>
          </li>
          <li className="ms-0 ms-md-3 ms-lg-2">
            <a href="mailto:trevor.phuong.tran@gmail.com">
              <IconButton aria-label="github">
                <EmailIcon />
              </IconButton>
            </a>
          </li>
        </ul>
      </header>
    </Box>
  );
}