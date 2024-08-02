/* eslint-disable react/jsx-key */
import React, { useState } from "react";
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

import styles from "../../components/Dashboard/Styles/DashboardStyles.js";
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';

const stripePromise = loadStripe('your-publishable-key');

const useStyles = makeStyles(styles);

function CheckoutForm({ amount, onPaymentSuccess }) {
  const stripe = useStripe();
  const elements = useElements();
  const [error, setError] = useState(null);
  const [processing, setProcessing] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!stripe || !elements) {
      return;
    }

    setProcessing(true);

    const { data: { clientSecret } } = await axios.post('/create-payment-intent', { amount });

    const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
      },
    });

    if (error) {
      setError(error.message);
      setProcessing(false);
    } else if (paymentIntent.status === 'succeeded') {
      setProcessing(false);
      onPaymentSuccess();
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <CardElement />
      {error && <div>{error}</div>}
      <button type="submit" disabled={!stripe || processing}>
        {processing ? 'Processing...' : 'Pay'}
      </button>
    </form>
  );
}

export default function LendRequests() {
  const classes = useStyles();
  const [searchTerm, setSearchTerm] = useState("");
  const [openConfirm, setOpenConfirm] = useState(false);
  const [openPayment, setOpenPayment] = useState(false);
  const [selectedLoanRequestId, setSelectedLoanRequestId] = useState(null);

  const handleClickOpenConfirm = (loanRequestId) => {
    setSelectedLoanRequestId(loanRequestId);
    setOpenConfirm(true);
  };

  const handleCloseConfirm = () => {
    setOpenConfirm(false);
    setSelectedLoanRequestId(null);
  };

  const handleConfirm = async () => {
    setOpenConfirm(false);
    setOpenPayment(true);
  };

  const handlePaymentSuccess = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/loanrequests/accept`, { loanRequestId: selectedLoanRequestId, acceptorId: userId })
      .then((response) => {
        console.log(response);
        fetchData();
      })
      .catch((err) => {
        console.log(err);
      });
    setOpenPayment(false);
  };

  const [data, setData] = useState([]);
  const fetchData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/loanrequests/exclude`, { userId: userId })
      .then(res => {
        setData(res.data);
      })
      .catch(err => {
        console.error("Error fetching data:", err);
      });
  };

  React.useEffect(() => {
    fetchData();
  }, []);

  const columns = [
    { id: 'createdAt', label: 'Date' },
    { id: 'amount', label: 'Amount' },
    { id: 'interestRate', label: 'Interest Rate' },
    { id: 'repaymentPeriod', label: 'Repayment Period' },
    { id: 'total', label: 'Total' },
    { id: 'requestedBy', label: 'Requested By'},
    { id: 'accept', label: 'Accept' }
  ];
  const rows = data;

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Loan Requests</h4>
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
                      <TableCell style={{ color: 'primary', backgroundColor: "white" }} key={column.id}>
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
                  }).map((row, id) => (
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
                      <TableCell align="center">
                      {row.requestedByFirstName+" "+row.requestedByLastName}
                      </TableCell>
                      <TableCell align="left">
                        <Button size="sm" color="primary" onClick={() => handleClickOpenConfirm(row.loanRequestId)}>Accept</Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableScrollbar>
          </CardBody>
        </Card>
      </GridItem>

      <Dialog onClose={handleCloseConfirm} aria-labelledby="confirm-dialog-title" open={openConfirm}>
        <DialogTitle id="confirm-dialog-title">
          Confirm Action
        </DialogTitle>
        <DialogContent dividers>
          Are you sure you want to accept it?
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

      <Dialog 
        onClose={() => setOpenPayment(false)} 
        aria-labelledby="payment-dialog-title" 
        open={openPayment}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle id="payment-dialog-title">
          Payment
        </DialogTitle>
        <DialogContent>
          <Elements stripe={stripePromise}>
            <CheckoutForm amount={10000} onPaymentSuccess={handlePaymentSuccess} />
          </Elements>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenPayment(false)} color="primary">
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    </GridContainer>
  );
}
