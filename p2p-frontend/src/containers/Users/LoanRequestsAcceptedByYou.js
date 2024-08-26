import React, { useState, useEffect } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import TableScrollbar from 'react-table-scrollbar';

// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
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
import Rating from 'react-rating-stars-component';
import styles from "../../components/Dashboard/Styles/DashboardStyles.js";
import { Link } from 'react-router-dom';

const useStyles = makeStyles(styles);

export default function LoanRequests() {
  const classes = useStyles();
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [rating, setRating] = useState(0);
  const [openRateDialog, setOpenRateDialog] = useState(false);
  const [openConfirm, setOpenConfirm] = useState(false);
  const [selectedLoanRequestId, setSelectedLoanRequestId] = useState(null);
  const [data, setData] = useState([]);

  // Fetch data function
  const fetchData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/loanrequests/acceptedbyyou`, { userId })
      .then(res => {
        setData(res.data);
      })
      .catch(err => {
        console.error("Error fetching data:", err);
      });
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleClickOpenConfirm = (loanRequestId) => {
    setSelectedLoanRequestId(loanRequestId);
    setOpenConfirm(true);
  };

  const handleCloseConfirm = () => {
    setOpenConfirm(false);
    setSelectedLoanRequestId(null);
  };

  const handleOpenRateDialog = (loanRequestId, userId) => {
    setSelectedLoanRequestId(loanRequestId);
    setSelectedUserId(userId);
    setOpenRateDialog(true);
  };

  const handleCloseRateDialog = () => {
    setOpenRateDialog(false);
    setSelectedLoanRequestId(null);
    setSelectedUserId(null);
  };

  const handleConfirm = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/loanrequests/accept`, { loanRequestId: selectedLoanRequestId, acceptorId: userId })
      .then(response => {
        console.log(response);
        fetchData(); 
      })
      .catch(err => {
        console.log(err);
      });
    setOpenConfirm(false);
  };

  const handleSubmitRating = () => {
    axios.post(`${backendUrl}/users/loanrequest/submitrating`, { 
      loanRequestId: selectedLoanRequestId, 
      userId: selectedUserId, 
      rating 
    })
    .then(response => {
      console.log("Rating saved:", response.data);
      setOpenRateDialog(false);
      fetchData(); 
    })
    .catch(err => {
      console.error("Error saving rating:", err);
    });
  };

  const columns = [
    { id: 'createdAt', label: 'Date' },
    { id: 'amount', label: 'Amount' },
    { id: 'interestRate', label: 'Interest Rate' },
    { id: 'repaymentPeriod', label: 'Repayment Period' },
    { id: 'total', label: 'Total' },
    { id: 'status', label: 'Status' },
    { id: 'Requestedby', label: 'Requested By' },
    { id: 'Action', label: 'Action' }
  ];

  const filteredRows = data.filter((row) => {
    if (searchTerm === "") return true;
    return row.userFirstName.toLowerCase().includes(searchTerm.toLowerCase()) || row.userLastName.toLowerCase().includes(searchTerm.toLowerCase());
  });

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Loan Requests Received</h4>
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
                      <TableCell key={column.id}>
                        {column.label}
                      </TableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredRows.map((row, id) => (
                    <TableRow key={id}>
                      <TableCell align="left">{row.createdAt}</TableCell>
                      <TableCell align="left">{row.amount}</TableCell>
                      <TableCell align="center">{row.interestRate}</TableCell>
                      <TableCell align="center">{row.repaymentPeriod}</TableCell>
                      <TableCell align="center">{row.total}</TableCell>
                      <TableCell align="left">{row.status}</TableCell>
                      <TableCell align="left">
                        <Link
                          to={`/user/viewuser/${row.userId}`}
                          style={{ textDecoration: 'underline', color: 'inherit' }}
                        >
                          {row.userFirstName + " " + row.userLastName}
                        </Link>
                      </TableCell>
                      <TableCell>
                        {(row.status === 'CLOSED' || row.status === 'CLOSED/Rated by Requester') && (
                          <Button size="sm" color="primary" onClick={() => handleOpenRateDialog(row.lendRequestId, row.userId)}>Rate</Button>
                        )}
                        {!(row.status === 'CLOSED' || row.status === 'CLOSED/Rated by Requester') && (
                          <Button size="sm" color="primary" onClick={() => handleClickOpenConfirm(row.lendRequestId)}>Accept</Button>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableScrollbar>
          </CardBody>
        </Card>
      </GridItem>

      {/* Confirm Dialog */}
      <Dialog onClose={handleCloseConfirm} aria-labelledby="confirm-dialog-title" open={openConfirm}>
        <DialogTitle id="confirm-dialog-title">Confirm Action</DialogTitle>
        <DialogContent dividers>
          Are you sure you want to accept this request?
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseConfirm} color="primary">No</Button>
          <Button onClick={handleConfirm} color="primary">Yes</Button>
        </DialogActions>
      </Dialog>

      {/* Rate Dialog */}
      <Dialog
        onClose={handleCloseRateDialog}
        aria-labelledby="rate-dialog-title"
        open={openRateDialog}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle id="rate-dialog-title">Rate This User</DialogTitle>
        <DialogContent dividers>
          <Rating
            count={5}
            size={48}
            value={rating}
            onChange={(newRating) => setRating(newRating)}
            activeColor="#ffd700"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseRateDialog} color="primary">Cancel</Button>
          <Button onClick={handleSubmitRating} color="primary">Submit</Button>
        </DialogActions>
      </Dialog>
    </GridContainer>
  );
}
