import React, { useState, useEffect } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import TableScrollbar from 'react-table-scrollbar';
import { makeStyles } from "@material-ui/core/styles";
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import TextField from '@material-ui/core/TextField';
import Button from "../../components/Dashboard/Button/Button.js";
import GridItem from "../../components/Dashboard/Grid/GridItem.js";
import GridContainer from "../../components/Dashboard/Grid/GridContainer.js";
import Card from "../../components/Dashboard/Card/Card.js";
import CardHeader from "../../components/Dashboard/Card/CardHeader.js";
import CardBody from "../../components/Dashboard/Card/CardBody.js";
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableRow from "@material-ui/core/TableRow";
import SearchIcon from '@material-ui/icons/Search';
import InputAdornment from '@material-ui/core/InputAdornment';
import styles from "../../components/Dashboard/Styles/DashboardStyles.js";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const useStyles = makeStyles(styles);

export default function AddLendRequest() {
  const classes = useStyles();
  const [amount, setAmount] = useState("");
  const [interestRate, setInterestRate] = useState("");
  const [repaymentPeriod, setRepaymentPeriod] = useState("");
  const [open, setOpen] = useState(false);
  const [total, setTotal] = useState(0); 
  const [errors, setErrors] = useState({});
  const [lendRequests, setLendRequests] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [deleteLendRequestId, setDeleteLendRequestId] = useState(null);
  const [data, setData] = useState([]);

  useEffect(() => {
    fetchLendRequests();
  }, []);

  useEffect(() => {
    calculateTotal();
  }, [amount, interestRate, repaymentPeriod]);

  const notifyError = (message) => toast.error(message, {
    position: "top-center",
    autoClose: 5000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
    progress: undefined,
  });

  const fetchLendRequests = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/lendrequests/pending`, { userId: userId })
      .then(res => {
        setData(res.data);
      })
      .catch(err => {
        console.error("Error fetching lend requests:", err);
      });
  };

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setAmount("");
    setInterestRate("");
    setRepaymentPeriod("");
    setErrors({});
  };

  const handleConfirmOpen = (lendRequestId) => {
    setDeleteLendRequestId(lendRequestId);
    setConfirmOpen(true);
  };

  const handleConfirmClose = () => {
    setConfirmOpen(false);
    setDeleteLendRequestId(null);
  };

  const handleDeleteConfirmed = () => {
    if (deleteLendRequestId !== null) {
      axios.post(`${backendUrl}/users/lendrequests/delete`, {
        lendRequestId: deleteLendRequestId,
      }).then(response => {
        console.log(response);
        fetchLendRequests();
        handleConfirmClose();
      }).catch(error => {
        console.error("There was an error deleting the lend request!", error);
        handleConfirmClose();
      });
    }
  };

  const validate = () => {
    let tempErrors = {};
    tempErrors.amount = amount < 500 || amount > 50000 ? "Amount must be between 500 and 50000" : "";
    tempErrors.interestRate = interestRate < 0 || interestRate > 25 ? "Interest rate must be between 0 and 25" : "";
    setErrors(tempErrors);
    return Object.values(tempErrors).every(x => x === "");
  };

  const handleSubmit = () => {
    if (!amount || !interestRate || !repaymentPeriod) {
      notifyError('All fields are required!');
      return;
    }

    if (validate()) {
      calculateTotal();
      const user = JSON.parse(window.localStorage.getItem('user'));
      const userId = user.userId;

      axios.post(`${backendUrl}/users/lendrequests/submit`, {
        userId: userId,
        amount: amount,
        interestRate: interestRate,
        repaymentPeriod: repaymentPeriod,
        total: total
      }).then(response => {
        console.log(response);
        fetchLendRequests();
        handleClose();
      }).catch(error => {
        console.error("There was an error creating the lend request!", error);
      });
    } else {
      notifyError('Please correct the errors in the form before submitting.');
    }
  };

  const calculateTotal = () => {
    const totalValue = (amount * (interestRate / 100) * (repaymentPeriod / 12)) + parseFloat(amount);
    setTotal(totalValue);
  };

  const columns = [
    { id: 'createdAt', label: 'Date' },
    { id: 'amount', label: 'Amount' },
    { id: 'interestRate', label: 'Interest Rate' },
    { id: 'repaymentPeriod', label: 'Repayment Period' },
    { id: 'total', label: 'Total' },
    { id: 'accept', label: 'Accept' },
  ];
  
  const rows = data;

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={6}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Add Lend Request</h4>
          </CardHeader>
          <CardBody>
            <Button color="primary" onClick={handleOpen}>
              Add New Lend Request
            </Button>
          </CardBody>
        </Card>
      </GridItem>
      <GridItem xs={12} sm={12} md={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Pending Lend Requests</h4>
          </CardHeader>
          <CardBody>
            <div>
              <FormControl fullWidth variant="outlined" size="small">
                <TextField
                  variant="outlined"
                  size="small"
                  placeholder="Search..."
                  onChange={(event) => setSearchTerm(event.target.value)}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <SearchIcon />
                      </InputAdornment>
                    ),
                  }}
                />
              </FormControl>
            </div>
            <TableScrollbar rows={10}>
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
                    } else if (row.createdAt.toLowerCase().includes(searchTerm.toLowerCase()) ||
                               row.amount.toString().toLowerCase().includes(searchTerm.toLowerCase())) {
                      return row;
                    }
                    return null;
                  }).map((row, id) => (
                    <TableRow key={id}>
                      <TableCell align="left">{row.createdAt}</TableCell>
                      <TableCell align="center">{row.amount}</TableCell>
                      <TableCell align="center">{row.interestRate}</TableCell>
                      <TableCell align="center">{row.repaymentPeriod}</TableCell>
                      <TableCell align="left">{row.total}</TableCell>
                      <TableCell align="left">
                        <Button size="sm" color="danger" onClick={() => handleConfirmOpen(row.lendRequestId)}>Delete</Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableScrollbar>
          </CardBody>
        </Card>
      </GridItem>

      <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Add New Loan Request</DialogTitle>
        <DialogContent>
        <TextField
            autoFocus
            margin="dense"
            id="amount"
            label="Amount"
            type="number"
            fullWidth
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            inputProps={{ min: 500, max: 50000 }}
            error={Boolean(errors.amount)}
            helperText={errors.amount}
          />
          <TextField
            margin="dense"
            id="interestRate"
            label="Interest Rate (%)"
            type="number"
            step="0.5"
            fullWidth
            value={interestRate}
            onChange={(e) => setInterestRate(e.target.value)}
            inputProps={{ min: 0, max: 25, step: 0.5 }}
            error={Boolean(errors.interestRate)}
            helperText={errors.interestRate}
          />
          <FormControl fullWidth margin="dense">
            <InputLabel id="repayment-period-label">Repayment Period (months)</InputLabel>
            <Select
              labelId="repayment-period-label"
              id="repaymentPeriod"
              value={repaymentPeriod}
              onChange={(e) => setRepaymentPeriod(e.target.value)}
            >
              {Array.from({ length: 22 }, (_, i) => i + 3).map(month => (
                <MenuItem key={month} value={month}>{month}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            margin="dense"
            id="total"
            label="Total"
            type="number"
            fullWidth
            value={total}
            disabled
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleSubmit} color="primary">
            Submit
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={confirmOpen} onClose={handleConfirmClose}>
        <DialogTitle>Are you sure you want to delete this request?</DialogTitle>
        <DialogActions>
          <Button onClick={handleConfirmClose} color="primary">
            No
          </Button>
          <Button onClick={handleDeleteConfirmed} color="primary">
            Yes
          </Button>
        </DialogActions>
      </Dialog>

      <ToastContainer />
    </GridContainer>
  );
}
