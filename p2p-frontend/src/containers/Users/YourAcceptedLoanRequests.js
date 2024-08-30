import React, { useState, useEffect } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import TableScrollbar from 'react-table-scrollbar';
import Rating from 'react-rating-stars-component'; 
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
import GridItem from "../../components/Dashboard/Grid/GridItem.js";
import GridContainer from "../../components/Dashboard/Grid/GridContainer.js";
import Card from "../../components/Dashboard/Card/Card.js";
import CardHeader from "../../components/Dashboard/Card/CardHeader.js";
import CardBody from "../../components/Dashboard/Card/CardBody.js";
import Button from "../../components/Dashboard/Button/Button.js";
import styles from "../../components/Dashboard/Styles/DashboardStyles.js";
import { Link } from 'react-router-dom';

const useStyles = makeStyles(styles);

export default function LoanRequests() {
  const classes = useStyles();
  const [data, setData] = useState([]);
  const [searchTerm, setSearchTerm] = useState(""); 
  const [openConfirm, setOpenConfirm] = React.useState(false);
  const [openRateDialog, setOpenRateDialog] = useState(false);
  const [selectedLoanRequestId, setSelectedLoanRequestId] = useState(null);
  const [selectedUserId, setSelectedUserId] = useState(null); 
  const [rating, setRating] = useState(0); 

  const handleClickOpenConfirm = (LoanRequestId) => {
    setSelectedLoanRequestId(LoanRequestId);
    setOpenConfirm(true);
  };

  const handleCloseConfirm = () => {
    setOpenConfirm(false);
    setSelectedLoanRequestId(null);
  };

  const handleConfirm = () => {
    if (!selectedLoanRequestId) {
      console.error("Loan Request ID must be provided");
      setOpenConfirm(false);
      return;
    }

    axios.post(`${backendUrl}/users/loanrequests/payback`, { loanRequestId: selectedLoanRequestId })
      .then(res => {
        console.log("Payback successful:", res.data);
        fetchData(); // Refresh data after successful payback
        setOpenConfirm(false);
      })
      .catch(err => {
        console.error("Error during payback:", err);
        setOpenConfirm(false);
      });
  };

  const handleOpenRateDialog = (LoanRequestId, acceptedUserId) => {
    setSelectedLoanRequestId(LoanRequestId);
    setSelectedUserId(acceptedUserId); 
    setOpenRateDialog(true);
  };

  const handleCloseRateDialog = () => {
    setOpenRateDialog(false);
    setSelectedLoanRequestId(null);
    setSelectedUserId(null); 
  };

  const handleSubmitRating = () => {
    axios.post(`${backendUrl}/users/loanrequest/submitrating`, { 
      loanRequestId: selectedLoanRequestId, 
      userId: selectedUserId, 
      rating: rating 
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

  const fetchData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/loanrequests/accepted`, { userId: userId })
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

  const columns = [
    { id: 'createdAt', label: 'Date' },
    { id: 'amount', label: 'Amount' },
    { id: 'interestRate', label: 'Interest Rate' },
    { id: 'repaymentPeriod', label: 'Repayment Period' },
    { id: 'total', label: 'Total' },
    { id: 'acceptedByFirstName', label: 'Accepted By' },
    { id: 'status', label: 'Status' },
    { id: 'action', label: 'Action' }
  ];

  const rows = data;

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Your Accepted Loan Requests</h4>
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
                  onChange={(event) => {
                    setSearchTerm(event.target.value);
                  }}
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
                      <TableCell style={{ color: 'primary', backgroundColor: "white" }}
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
                    } else if (row.name.toLowerCase().includes(searchTerm.toLowerCase()) || row.email.toLowerCase().includes(searchTerm.toLowerCase())) {
                      return row;
                    }
                  }).map((row, id) => {
                    return (
                      <TableRow key={id}>
                        <TableCell align="left">
                          {row.createdAt}
                        </TableCell>
                        <TableCell align="left">
                          {row.amount}
                        </TableCell>
                        <TableCell align="center">
                          {row.interestRate}
                        </TableCell>
                        <TableCell align="center">
                          {row.repaymentPeriod}
                        </TableCell>
                        <TableCell align="center">
                          {row.total}
                        </TableCell>
                        <TableCell align="left">
                          <Link
                            to={`/user/viewuser/${row.acceptedUserId}`}
                            style={{ textDecoration: 'underline', color: 'inherit' }}
                          >
                            {row.acceptedByFirstName + " " + row.acceptedByLastName}
                          </Link>
                        </TableCell>
                        <TableCell align="left">
                          {row.status}
                        </TableCell>
                        <TableCell align="left">
                          {row.status === 'PAID' && (
                            <Button size="sm" color="danger" onClick={() => handleClickOpenConfirm(row.loanRequestId)}>Pay Back</Button>
                          )}
                          {(row.status === 'CLOSED' || row.status ==='CLOSED/Rated by Acceptor')&& (
                            <Button size="sm" color="primary" onClick={() => handleOpenRateDialog(row.loanRequestId, row.acceptedUserId)}>Rate</Button>
                          )}
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </TableScrollbar>
          </CardBody>
        </Card>
      </GridItem>

      {/* Confirm Dialog */}
      <Dialog onClose={handleCloseConfirm} aria-labelledby="confirm-dialog-title" open={openConfirm}>
        <DialogTitle id="confirm-dialog-title">
          Confirm Action
        </DialogTitle>
        <DialogContent dividers>
          Are you sure you want to pay back this loan?
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseConfirm} color="primary">
            No
          </Button>
          <Button onClick={handleConfirm} color="primary">
            Yes
          </Button>
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
        <DialogTitle id="rate-dialog-title">
          Rate This User
        </DialogTitle>
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
          <Button onClick={handleCloseRateDialog} color="primary">
            Cancel
          </Button>
          <Button onClick={handleSubmitRating} color="primary">
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    </GridContainer>
  );
}
