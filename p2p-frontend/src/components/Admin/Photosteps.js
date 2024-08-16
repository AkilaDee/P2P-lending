import React from 'react';
import PropTypes from "prop-types";
import { makeStyles, useTheme } from '@material-ui/core/styles';
import MobileStepper from '@material-ui/core/MobileStepper';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import KeyboardArrowLeft from '@material-ui/icons/KeyboardArrowLeft';
import KeyboardArrowRight from '@material-ui/icons/KeyboardArrowRight';

const useStyles = makeStyles((theme) => ({
  root: {
    maxWidth: 400,
    flexGrow: 1,
  },
  header: {
    display: 'flex',
    alignItems: 'center',
    height: 50,
    paddingLeft: theme.spacing(4),
    backgroundColor: theme.palette.background.default,
  },
  img: {
    maxWidth: 600,
    overflow: 'hidden',
    display: 'block',
    width: '100%',
  },
}));

export default function TextMobileStepper({ doc1, doc2, doc3, doc4 }) {
  const classes = useStyles();
  const theme = useTheme();
  const images = [doc1, doc2, doc3, doc4].filter(Boolean); // Filter out undefined or null values
  const maxSteps = images.length;

  const [activeStep, setActiveStep] = React.useState(0);

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const labels = ['Credit Score', 'Financial Document/Bank Statement', 'Proof of Address', 'Proof of ID'];

  return (
    <div className={classes.root}>
      <Paper square elevation={0} className={classes.header}>
        <Typography>{labels[activeStep]}</Typography>
      </Paper>
      {images[activeStep] ? (
        <img
          className={classes.img}
          src={`data:image/jpeg;base64,${images[activeStep]}`}
          alt={labels[activeStep]}
        />
      ) : (
        <Typography>No image available</Typography>
      )}
      <MobileStepper
        steps={maxSteps}
        position="static"
        variant="text"
        activeStep={activeStep}
        nextButton={
          <Button size="small" onClick={handleNext} disabled={activeStep === maxSteps - 1}>
            Next
            {theme.direction === 'rtl' ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}
          </Button>
        }
        backButton={
          <Button size="small" onClick={handleBack} disabled={activeStep === 0}>
            {theme.direction === 'rtl' ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
            Back
          </Button>
        }
      />
    </div>
  );
}

TextMobileStepper.propTypes = {
  doc1: PropTypes.string,
  doc2: PropTypes.string,
  doc3: PropTypes.string,
  doc4: PropTypes.string,
};
