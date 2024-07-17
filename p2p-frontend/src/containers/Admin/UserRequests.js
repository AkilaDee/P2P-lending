import React, { useState, useEffect } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import TableScrollbar from 'react-table-scrollbar';

// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogActions from '@material-ui/core/DialogActions';
import TextField from '@material-ui/core/TextField';
import FormControl from '@material-ui/core/FormControl';
import InputAdornment from '@material-ui/core/InputAdornment';
import OutlinedInput from '@material-ui/core/OutlinedInput';
import { Table, TableHead, TableBody, TableCell, TableRow } from "@material-ui/core";

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
  const [searchTerm, setSearchTerm] = useState(""); // for search function

  // Popup dialogbox
  const [openReject, setOpenReject] = useState(false);
  const handleClickOpenReject = (userId, email) => {
    setOpenReject(true);
    setUserId(userId);
    setEmail(email);
  };
  const handleCloseReject = () => {
    setOpenReject(false);
  };
  
  // Backend connection for reject user
  const [userId, setUserId] = useState();
  const [rejectReason, setRejectReason] = useState();
  const [email, setEmail] = useState();
  const rejectUser = () => {
    axios.post(`${backendUrl}/admin/users/delete`, { userId, rejectReason, email })
      .then((response) => {
        console.log(response);
        getData();
        handleCloseReject();
      })
      .catch((err) => {
        console.log(err);
      });
  };
  
  // Backend connection for accept user
  const acceptUser = (userId) => {
    axios.post(`${backendUrl}/admin/users/accept`, { userId })
      .then((response) => {
        console.log(response);
        getData();
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const [open, setOpen] = useState(false);
  const [doc1, setDoc1] = useState('');
  const [doc2, setDoc2] = useState('');
  const [doc3, setDoc3] = useState('');
  const [doc4, setDoc4] = useState('');


  const handleClickOpen = (document1, document2, document3,document4) => {
    setDoc1(document1);
    setDoc2(document2);
    setDoc3(document3);
    setDoc4(document4)
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  // Backend connection
  const [data, setData] = useState([]);
  const getData = () => {
    axios.get(`${backendUrl}/admin/users/inactive`)
      .then(res => {
        const results = res.data;
        setData(results); // Ensure data is an array
        console.log(res);
      })
      .catch(err => {
        console.log(err);
        setData([]); // Set empty array on error
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
  
  const rows = data || []; // Fallback to empty array if data is undefined

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
            <TableScrollbar rows={20}>
              <Table>
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
                      {/* <TableCell align="left">
                        {row.contactnumber}
                      </TableCell> */}
                      <TableCell align="left">
                        <Button size='sm' color="primary" onClick={() => handleClickOpen(row.document1, row.document2, row.document3, row.document4)}>View</Button>
                      </TableCell>
                      <TableCell>
                        <Button size='sm' color="primary" onClick={() => acceptUser(row.userId)}>Accept</Button>
                      </TableCell>
                      <TableCell align="left">
                        <Button size='sm' color="danger" onClick={() => handleClickOpenReject(row.userId)}>Reject</Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableScrollbar>
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
            id="name"
            onChange={(e) => setRejectReason(e.target.value)}
            label="State the reason"
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseReject} color="primary">
            Cancel
          </Button>
          <Button onClick={rejectUser} color="primary">
            Send
          </Button>
        </DialogActions>
      </Dialog>
    </GridContainer>
  );
}
