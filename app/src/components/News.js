import { Box, Grid, Typography } from "@mui/material";

export default function News(props) {
  const { imageUrl, url, summary, publishedDate, title } = props;

  return (
    <Box className="d-flex my-2 w-100 px-2 row">
      <Box className="col-1">
        <img className="img-thumbnail"
          style={{width: "70px", height: "50px"}}
          src={imageUrl ? imageUrl : (process.env.PUBLIC_URL + "/no-picture.png")}
          alt={title} />
      </Box>

      <Box className="col-11">
        <div className="text-truncate">
          <a className="fs-6 fw-bold text-capitalize" href={url} target="_blank">{title}</a>
          </div>
        <span className="d-block small text-secondary text-truncate">{summary}</span>
      </Box>
    </Box>
  )
}