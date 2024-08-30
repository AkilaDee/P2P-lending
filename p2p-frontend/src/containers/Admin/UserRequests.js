import React, { useState, useEffect } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';

// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import TextField from '@material-ui/core/TextField';
import FormControl from '@material-ui/core/FormControl';
import InputAdornment from '@material-ui/core/InputAdornment';
import OutlinedInput from '@material-ui/core/OutlinedInput';
import { Table, TableHead, TableBody, TableCell, TableRow, TableContainer, Paper } from "@material-ui/core";
import DialogContentText from '@material-ui/core/DialogContentText';

import SearchIcon from '@material-ui/icons/Search';

// core components
import GridItem from "../../components/Dashboard/Grid/GridItem.js";
import GridContainer from "../../components/Dashboard/Grid/GridContainer.js";
import Card from "../../components/Dashboard/Card/Card.js";
import CardHeader from "../../components/Dashboard/Card/CardHeader.js";
import CardBody from "../../components/Dashboard/Card/CardBody.js";
import Button from "../../components/Dashboard/Button/Button.js";
import PhotoSteps from "../../components/Admin/Photosteps.js";

import styles from "../../components/Dashboard/Styles/DashboardStyles.js";

const useStyles = makeStyles(styles);

export default function UserRequests() {
  const classes = useStyles();
  const [searchTerm, setSearchTerm] = useState(""); // For search function

  // Popup dialog box for rejection
  const [openReject, setOpenReject] = useState(false);
  const [userId, setUserId] = useState();
  const [rejectReason, setRejectReason] = useState();
  const [email, setEmail] = useState();

  const handleClickOpenReject = (userId, email) => {
    setOpenReject(true);
    setUserId(userId);
    setEmail(email);
  };
  const handleCloseReject = () => {
    setOpenReject(false);
  };
  
  const rejectUser = () => {
    axios.post(`${backendUrl}/admin/users/delete`, { userId, rejectReason, email })
      .then((response) => {
        console.log(response);
        getData();
        handleCloseReject();
      })
      .catch((err) => {
        console.log(err);
        handleCloseReject();
      });
  };

  // Popup dialog box for acceptance
  const [openConfirm, setOpenConfirm] = useState(false);
  const [confirmUserId, setConfirmUserId] = useState(null);

  const handleClickOpenConfirm = (userId) => {
    setConfirmUserId(userId);
    setOpenConfirm(true);
  };
  const handleCloseConfirm = () => {
    setOpenConfirm(false);
  };

  const acceptUserConfirmed = () => {
    if (confirmUserId !== null) {
      axios.post(`${backendUrl}/admin/users/accept`, { userId: confirmUserId })
        .then((response) => {
          console.log(response);
          getData();
          handleCloseConfirm();
        })
        .catch((err) => {
          console.log(err);
          handleCloseConfirm();
        });
    }
  };

  // Popup dialog box for viewing documents
  const [open, setOpen] = useState(false);
  const [doc1, setDoc1] = useState('');
  const [doc2, setDoc2] = useState('');
  const [doc3, setDoc3] = useState('');
  const [doc4, setDoc4] = useState('');

  const handleClickOpen = (document1, document2, document3, document4) => {
    setDoc1(document1);
    setDoc2(document2);
    setDoc3(document3);
    setDoc4(document4);
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  // Convert file to Base64
  const convertToBase64 = (file, setState) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => {
      setState(reader.result); // Update the state with Base64 string
    };
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Convert to Base64 and update the state
      convertToBase64(file, setDoc1); 
      convertToBase64(file, setDoc2); 
      convertToBase64(file, setDoc3); 
      convertToBase64(file, setDoc4); 

    }
  };

  // Fetch inactive users from the backend
  const [data, setData] = useState([]);
  const getData = () => {
    axios.get(`${backendUrl}/admin/users/inactive`)
      .then(res => {
        const results = res.data;
        setData(results); 
        console.log(res);
      })
      .catch(err => {
        console.log(err);
        setData([]); 
      });
  };

  useEffect(() => {
    getData();
  }, []);

  const columns = [
    { id: 'firstName', label: 'First Name' },
    { id: 'lastName', label: 'Last Name' },
    { id: 'email', label: 'Email' },
    { id: 'document', label: 'Documents' },
    { id: 'activate', label: 'Activate' },
    { id: 'reject', label: 'Reject' },
  ];

  const rows = data || []; 

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>User Registration Requests</h4>
          </CardHeader>
          <CardBody>
            <div>
              <FormControl fullWidth variant="outlined" size="small">
                <OutlinedInput
                  endAdornment={
                    <InputAdornment position="end">
                      <SearchIcon />
                    </InputAdornment>
                  }
                  onChange={(event) => setSearchTerm(event.target.value)}
                  placeholder="Search..."
                  fontSize="small"
                  size="sm"
                />
              </FormControl>
            </div>
            <TableContainer component={Paper} style={{ maxHeight: 440 }}>
              <Table stickyHeader aria-label="sticky table">
                <TableHead>
                  <TableRow>
                    {columns.map((column) => (
                      <TableCell
                        style={{ color: 'primary', backgroundColor: "white" }}
                        key={column.id}
                      >
                        {column.label}
                      </TableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {rows.filter((row) => {
                    if (searchTerm === "") {
                      return row;
                    } else if ((row.firstName && row.firstName.toLowerCase().includes(searchTerm.toLowerCase())) || 
                               (row.lastName && row.lastName.toLowerCase().includes(searchTerm.toLowerCase())) || 
                               (row.email && row.email.toLowerCase().includes(searchTerm.toLowerCase()))) {
                      return row;
                    }
                    return null;
                  }).map((row, id) => (
                    <TableRow key={id}>
                      <TableCell align="left">
                        {row.firstName}
                      </TableCell>
                      <TableCell align="left">
                        {row.lastName}
                      </TableCell>
                      <TableCell align="left">
                        {row.email}
                      </TableCell>
                      <TableCell align="left">
                        <Button size='sm' color="primary" onClick={() => handleClickOpen(row.proofOfId, row.proofOfAddress, row.financialInfo, row.creditScore)}>View</Button>
                      </TableCell>
                      <TableCell>
                        <Button size='sm' color="primary" onClick={() => handleClickOpenConfirm(row.userId)}>Accept</Button>
                      </TableCell>
                      <TableCell align="left">
                        <Button size='sm' color="danger" onClick={() => handleClickOpenReject(row.userId, row.email)}>Reject</Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardBody>
        </Card>
      </GridItem>
      
      {/* View documents dialog box */}
      <Dialog onClose={handleClose} aria-labelledby="customized-dialog-title" open={open}>
        <DialogTitle id="customized-dialog-title" onClose={handleClose}>
          Documents
        </DialogTitle>
        <DialogContent dividers>
          <PhotoSteps doc1={doc1} doc2={doc2} doc3={doc3} doc4={doc4}/>
        </DialogContent>
        <DialogActions>
          <Button autoFocus onClick={handleClose} color="primary">
            Okay
          </Button>
        </DialogActions>
      </Dialog>

      {/* Reject button dialog box */}
      <Dialog open={openReject} onClose={handleCloseReject} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Reason to Reject the Request</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {"Reasons to reject the request"}<br></br>
            {"1. Documents are not clear. Please re-register with necessary documents"}<br></br>
            {"2. Documents are not valid"}<br></br>
            {"3. Documents are overdue."}<br></br>
            {"4. Wrong information provided."}<br></br>
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            label="Reason"
            type="text"
            fullWidth
            variant="outlined"
            onChange={(e) => setRejectReason(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseReject} color="primary">
            Cancel
          </Button>
          <Button onClick={rejectUser} color="primary">
            Submit
          </Button>
        </DialogActions>
      </Dialog>

      {/* Accept button confirmation dialog box */}
      <Dialog
        open={openConfirm}
        onClose={handleCloseConfirm}
        aria-labelledby="confirmation-dialog-title"
      >
        <DialogTitle id="confirmation-dialog-title">
          Confirm Acceptance
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to accept this user registration request?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseConfirm} color="primary">
            Cancel
          </Button>
          <Button onClick={acceptUserConfirmed} color="primary">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </GridContainer>
  );
}
