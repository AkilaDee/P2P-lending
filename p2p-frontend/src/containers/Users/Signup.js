import React, { useState } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import { Redirect } from 'react-router-dom'; // Import Redirect
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function Copyright(props) {
  return (
    <Typography variant="body2" color="text.secondary" align="center" {...props}>
      {'Copyright © '}
      <Link color="inherit" href="https://mui.com/">
        Your Website
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

const defaultTheme = createTheme();

export default function SignUp() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [proofOfId, setProofOfId] = useState(null);
  const [proofOfAddress, setProofOfAddress] = useState(null);
  const [financialInfo, setFinancialInfo] = useState(null);
  const [creditScore, setCreditScore] = useState(null);
  const [redirect, setRedirect] = useState(false); // State to control redirection

  const notifyError = (message) => toast.error(message, {
    position: "top-center",
    autoClose: 5000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
    progress: undefined,
  });

  const handleFileChange = (setter) => (event) => {
    setter(event.target.files[0]);
  };

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    // Validation logic
    if (!firstName || !lastName || !email || !password || !proofOfId || !proofOfAddress || !financialInfo || !creditScore) {
      notifyError('All fields are required!');
      return;
    }

    if (password.length < 6) {
      notifyError('Password must be at least 6 characters long!');
      return;
    }

    if (!validateEmail(email)) {
      notifyError('Please enter a valid email address!');
      return;
    }

    const form = new FormData();
    const userDto = JSON.stringify({
      firstName: firstName,
      lastName: lastName,
      email: email,
      password: password
    });

    form.append("userDto", new Blob([userDto], { type: "application/json" }));
    form.append("proofOfIdFile", proofOfId);
    form.append("proofOfAddressFile", proofOfAddress);
    form.append("financialInfoFile", financialInfo);
    form.append("creditScoreFile", creditScore);

    axios.post(`${backendUrl}/users/signup`, form, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    .then((response) => {
      console.log(response);
      setRedirect(true); // Set redirect state to true on success
    })
    .catch((err) => {
      console.log(err);
      notifyError('Signup Error!');
    });
  };

  // If redirect state is true, redirect to the dashboard
  if (redirect) {
    return <Redirect to="/" />;
  }

  return (
    <ThemeProvider theme={defaultTheme}>
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <Box
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            Sign up
          </Typography>
          <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  autoComplete="given-name"
                  name="firstName"
                  required
                  fullWidth
                  id="firstName"
                  label="First Name"
                  autoFocus
                  onChange={(e) => setFirstName(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="lastName"
                  label="Last Name"
                  name="lastName"
                  autoComplete="family-name"
                  onChange={(e) => setLastName(e.target.value)}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  id="email"
                  label="Email Address"
                  name="email"
                  autoComplete="email"
                  onChange={(e) => setEmail(e.target.value)}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  name="password"
                  label="Password"
                  type="password"
                  id="password"
                  autoComplete="new-password"
                  onChange={(e) => setPassword(e.target.value)}
                />
              </Grid>
              <Grid item xs={12} sm={5} md={5}>
                <Typography variant="body2" display="block" align='left'>
                  Credit Score *
                </Typography>
              </Grid>
              <Grid item xs={12} sm={7} md={7}>
                <input
                  accept="image/*"
                  onChange={handleFileChange(setCreditScore)}
                  id="credit-score-file"
                  type="file"
                />
              </Grid>
              <Grid item xs={12} sm={5} md={5}>
                <Typography variant="body2" display="block" align='left'>
                  Financial Information *
                </Typography>
              </Grid>
              <Grid item xs={12} sm={7} md={7}>
                <input
                  accept="image/*"
                  onChange={handleFileChange(setFinancialInfo)}
                  id="financial-info-file"
                  type="file"
                />
              </Grid>
              <Grid item xs={12} sm={5} md={5}>
                <Typography variant="body2" display="block" align='left'>
                  Proof of Identification *
                </Typography>
              </Grid>
              <Grid item xs={12} sm={7} md={7}>
                <input
                  accept="image/*"
                  onChange={handleFileChange(setProofOfId)}
                  id="proof-id-file"
                  type="file"
                />
              </Grid>
              <Grid item xs={12} sm={5} md={5}>
                <Typography variant="body2" display="block" align='left'>
                  Proof of Address *
                </Typography>
              </Grid>
              <Grid item xs={12} sm={7} md={7}>
                <input
                  accept="image/*"
                  onChange={handleFileChange(setProofOfAddress)}
                  id="proof-address-file"
                  type="file"
                />
              </Grid>
              
            </Grid>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Sign Up
            </Button>
            <Grid container justifyContent="flex-end">
              <Grid item>
                <Link href="/login" variant="body2">
                  Already have an account? Sign in
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
        <Copyright sx={{ mt: 5 }} />
        <Box xs={12} sm={12} md={12}>
        {/* <Copyright /> */}
      </Box>
      <ToastContainer position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
      </Container>
    </ThemeProvider>
  );
}
