import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import TableScrollbar from 'react-table-scrollbar';

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import FormControl from '@material-ui/core/FormControl';
import InputAdornment from '@material-ui/core/InputAdornment';
import OutlinedInput from '@material-ui/core/OutlinedInput';
import { Table, TableHead, TableBody, TableCell, TableRow } from '@material-ui/core';

import SearchIcon from '@material-ui/icons/Search';

// core components
import GridItem from '../../components/Dashboard/Grid/GridItem.js';
import GridContainer from '../../components/Dashboard/Grid/GridContainer.js';
import Card from '../../components/Dashboard/Card/Card.js';
import CardHeader from '../../components/Dashboard/Card/CardHeader.js';
import CardBody from '../../components/Dashboard/Card/CardBody.js';
import Button from '../../components/Dashboard/Button/Button.js';

import styles from '../../components/Dashboard/Styles/DashboardStyles.js';

const useStyles = makeStyles(styles);

export default function Dashboard() {
  const classes = useStyles();
  const [searchTerm, setSearchTerm] = useState("");
  const [dataLoan, setDataLoan] = useState([]);
  const [dataLend, setDataLend] = useState([]);
  const [openConfirm, setOpenConfirm] = useState(false);
  const [selectedRequestId, setSelectedRequestId] = useState(null);
  const [requestType, setRequestType] = useState("");

  useEffect(() => {
    fetchLoanData();
    fetchLendData();
  }, []);

  const fetchLoanData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/loanrequests/exclude`, { userId })
      .then(res => {
        setDataLoan(res.data);
      })
      .catch(err => {
        console.error('Error fetching loan data:', err);
      });
  };

  const fetchLendData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/lendrequests/exclude`, { userId })
      .then(res => {
        setDataLend(res.data);
      })
      .catch(err => {
        console.error('Error fetching lend data:', err);
      });
  };

  const handleClickOpenConfirm = (requestId, type) => {
    setSelectedRequestId(requestId);
    setRequestType(type);
    setOpenConfirm(true);
  };

  const handleCloseConfirm = () => {
    setOpenConfirm(false);
    setSelectedRequestId(null);
    setRequestType("");
  };

  const handleConfirm = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    const url = requestType === "loan"
      ? `${backendUrl}/users/loanrequests/accept`
      : `${backendUrl}/users/lendrequests/accept`;

    axios.post(url, { requestId: selectedRequestId, acceptorId: userId })
      .then(response => {
        console.log(response);
        if (requestType === "loan") fetchLoanData();
        else fetchLendData();
      })
      .catch(err => {
        console.error('Error accepting request:', err);
      });

    setOpenConfirm(false);
  };

  const columnsLoan = [
    { id: 'createdAt', label: 'Date' },
    { id: 'amount', label: 'Amount' },
    { id: 'interestRate', label: 'Interest Rate' },
    { id: 'repaymentPeriod', label: 'Repayment Period' },
    { id: 'total', label: 'Total' },
    { id: 'accept', label: 'Accept' }
  ];

  const columnsLend = [
    { id: 'createdAt', label: 'Date' },
    { id: 'amount', label: 'Amount' },
    { id: 'interestRate', label: 'Interest Rate' },
    { id: 'repaymentPeriod', label: 'Repayment Period' },
    { id: 'total', label: 'Total' },
    { id: 'accept', label: 'Accept' }
  ];

  return (
    <GridContainer className={classes.dashboardContainer}>
      {/* Loan Requests Table */}
      <GridItem xs={12}>
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
                  onChange={(event) => setSearchTerm(event.target.value)}
                  placeholder="Search..."
                  fontSize="small"
                  size="sm"
                />
              </FormControl>
            </div>
            <div className={classes.tableContainer}>
              <TableScrollbar rows={20}>
                <Table className={classes.table}>
                  <TableHead>
                    <TableRow>
                      {columnsLoan.map((column) => (
                        <TableCell style={{ color: 'primary', backgroundColor: "white" }} key={column.id}>
                          {column.label}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {dataLoan.filter(row => searchTerm === "" || row.createdAt.toLowerCase().includes(searchTerm.toLowerCase())).map((row, id) => (
                      <TableRow key={id}>
                        <TableCell align="left">{row.createdAt}</TableCell>
                        <TableCell align="left">{row.amount}</TableCell>
                        <TableCell align="center">{row.interestRate}</TableCell>
                        <TableCell align="center">{row.repaymentPeriod}</TableCell>
                        <TableCell align="center">{row.total}</TableCell>
                        <TableCell align="left">
                          <Button size="sm" color="primary" onClick={() => handleClickOpenConfirm(row.loanRequestId, "loan")}>Accept</Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableScrollbar>
            </div>
          </CardBody>
        </Card>
      </GridItem>

      {/* Lend Requests Table */}
      <GridItem xs={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Lend Requests</h4>
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
            <div className={classes.tableContainer}>
              <TableScrollbar rows={20}>
                <Table className={classes.table}>
                  <TableHead>
                    <TableRow>
                      {columnsLend.map((column) => (
                        <TableCell style={{ color: 'primary', backgroundColor: "white" }} key={column.id}>
                          {column.label}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {dataLend.filter(row => searchTerm === "" || row.createdAt.toLowerCase().includes(searchTerm.toLowerCase())).map((row, id) => (
                      <TableRow key={id}>
                        <TableCell align="left">{row.createdAt}</TableCell>
                        <TableCell align="left">{row.amount}</TableCell>
                        <TableCell align="center">{row.interestRate}</TableCell>
                        <TableCell align="center">{row.repaymentPeriod}</TableCell>
                        <TableCell align="center">{row.total}</TableCell>
                        <TableCell align="left">
                          <Button size="sm" color="primary" onClick={() => handleClickOpenConfirm(row.lendRequestId, "lend")}>Accept</Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableScrollbar>
            </div>
          </CardBody>
        </Card>
      </GridItem>

      {/* Confirm Dialog */}
      <Dialog onClose={handleCloseConfirm} aria-labelledby="confirm-dialog-title" open={openConfirm}>
        <DialogTitle id="confirm-dialog-title">Confirm Request</DialogTitle>
        <DialogContent>
          Are you sure you want to accept this request?
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseConfirm} color="primary">
            Cancel
          </Button>
          <Button onClick={handleConfirm} color="primary">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </GridContainer>
  );
}
