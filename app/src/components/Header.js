import { Box, IconButton } from '@mui/material';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import GitHubIcon from '@mui/icons-material/GitHub';
import EmailIcon from '@mui/icons-material/Email';

export default function Header(props) {
  const { color } = props;
  return (
    <Box className="col-12 col-lg-10 col-xxl-8 m-auto">
      <header className="d-flex flex-wrap justify-content-center align-items-center">
        <a href="/" className="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-decoration-none">
          <span className="fs-4">Stock Comparator <sup className="fs-6">beta</sup></span>
        </a>
        <ul className="nav col-md-4 justify-content-end list-unstyled d-flex">
          <li className="ms-3">
            <a href="https://www.linkedin.com/in/trevorpt/" target="_blank" rel="noreferrer">
              <IconButton aria-label="linkedin">
                <LinkedInIcon />
              </IconButton>
            </a>
          </li>
          <li className="ms-3">
            <a href="https://github.com/trevor-tran" target="_blank" rel="noreferrer">
              <IconButton aria-label="github">
                <GitHubIcon />
              </IconButton>
            </a>
          </li>
          <li className="ms-3">
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