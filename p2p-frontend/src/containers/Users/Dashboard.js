import * as React from 'react';
import PropTypes from 'prop-types';
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import AutoAwesomeRoundedIcon from '@mui/icons-material/AutoAwesomeRounded';
import AppAppBar from './../../components/mainLandingPage/AppAppBar';
import Features from './../../components/mainLandingPage/Features';
import Testimonials from './../../components/mainLandingPage/Testimonials';
import Highlights from './../../components/mainLandingPage/Highlights';
import FAQ from './../../components/mainLandingPage/Footer';
import Footer from './../../components/mainLandingPage/Footer';
import getLPTheme from './../../components/mainLandingPage/GetLpTheme';
import pf from './../../components/Dashboard/Images/peerfund.png'; // Ensure the image is correctly imported

function ToggleCustomTheme({ showCustomTheme, toggleCustomTheme }) {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        width: '100dvw',
        position: 'fixed',
        bottom: 24,
      }}
    >
      <ToggleButtonGroup
        color="primary"
        exclusive
        value={showCustomTheme}
        onChange={toggleCustomTheme}
        aria-label="Platform"
        sx={{
          backgroundColor: 'background.default',
          '& .Mui-selected': {
            pointerEvents: 'none',
          },
        }}
      >
        <ToggleButton value>
          <AutoAwesomeRoundedIcon sx={{ fontSize: '20px', mr: 1 }} />
          Custom theme
        </ToggleButton>
        <ToggleButton value={false}>Material Design 2</ToggleButton>
      </ToggleButtonGroup>
    </Box>
  );
}

ToggleCustomTheme.propTypes = {
  showCustomTheme: PropTypes.shape({
    valueOf: PropTypes.func.isRequired,
  }).isRequired,
  toggleCustomTheme: PropTypes.func.isRequired,
};

export default function LandingPage() {
  const [mode, setMode] = React.useState('light');
  const [showCustomTheme, setShowCustomTheme] = React.useState(true);
  const LPtheme = createTheme(getLPTheme(mode));
  const defaultTheme = createTheme({ palette: { mode } });

  const toggleColorMode = () => {
    setMode((prev) => (prev === 'dark' ? 'light' : 'dark'));
  };

  const toggleCustomTheme = () => {
    setShowCustomTheme((prev) => !prev);
  };

  return (
    <ThemeProvider theme={showCustomTheme ? LPtheme : defaultTheme}>
      <CssBaseline />
      {/* <AppAppBar mode={mode} toggleColorMode={toggleColorMode} /> */}
      <Box sx={{ height: '100vh', overflowY: 'auto', bgcolor: 'background.default' }}>
        {/* Add the image element here */}
        {/* <Box sx={{ display: 'flex', justifyContent: 'center', mt: 6, mb: 2 }}>
          <img 
            src={pf} 
            alt="Peerfund logo" 
            style={{ maxWidth: '50%', height: 'auto' }} 
          />
        </Box> */}
        <Features />
        <Divider />
        <Testimonials />
        <Divider />
        <Highlights />
        {/* <Divider />
        <FAQ />
        <Divider /> */}
        {/* <Footer /> */}
      </Box>
      <ToggleCustomTheme
        showCustomTheme={showCustomTheme}
        toggleCustomTheme={toggleCustomTheme}
      />
    </ThemeProvider>
  );
}
