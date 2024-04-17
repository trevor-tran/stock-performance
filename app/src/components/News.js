import { Box} from "@mui/material";

export default function News(props) {
  const { imageUrl, url, summary, title } = props;

  return (
    <Box className="d-flex my-2 w-100 px-2 row">
      <Box className="d-none d-lg-block col-lg-1">
        <img className="img-thumbnail" alt=""
          style={{width: "70px", height: "50px"}}
          src={imageUrl ? imageUrl : (process.env.PUBLIC_URL + "/no-picture.png")}
        />
      </Box>

      <Box className="col-12 col-lg-11">
        <div className="text-truncate">
          <a className="fs-6 fw-bold text-capitalize" href={url} target="_blank" rel="noreferrer">{title}</a>
          </div>
        <span className="d-block small text-secondary text-truncate">{summary}</span>
      </Box>
    </Box>
  )
}