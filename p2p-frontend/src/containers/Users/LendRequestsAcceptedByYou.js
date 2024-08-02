/* eslint-disable react/jsx-key */
import React, { useState } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js'
import TableScrollbar from 'react-table-scrollbar'

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
// import PhotoSteps from "../../components/admin/dialogbox/PhotoSteps";

import styles from "../../components/Dashboard/Styles/DashboardStyles.js";

const useStyles = makeStyles(styles);

export default function LendRequests() {
  const classes = useStyles();
  const [searchTerm, setSearchTerm] = useState(""); //for search function

  const [openConfirm, setOpenConfirm] = React.useState(false);
  const [selectedLendRequestId, setSelectedLendRequestId] = useState(null);

  // Function to open the confirm dialog
  const handleClickOpenConfirm = (lendRequestId) => {
    setSelectedLendRequestId(lendRequestId);
    setOpenConfirm(true);
  };

  // Function to close the confirm dialog
  const handleCloseConfirm = () => {
    setOpenConfirm(false);
    setSelectedLendRequestId(null);
  };

  // Function to handle the confirm action
  const handleConfirm = () => {
    // const user = JSON.parse(window.localStorage.getItem('user'));
    // const userId = user.userId;
    axios.post(`${backendUrl}/users/lendrequests/payback`, { lendRequestId: selectedLendRequestId })
      .then((response) => {
        console.log(response);
        fetchData();
      })
      .catch((err) => {
        console.log(err);
      });
    setOpenConfirm(false);
  };

  // Backend connection
  const [data, setData] = useState([]);
  const fetchData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/lendrequests/acceptedbyyou`, { userId: userId })
      .then(res => {
        setData(res.data); // Set the received data
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
    { id: 'status', label: 'Status' },
    { id: 'Requestedby', label: 'Requested By' },
    { id: 'Action', label: 'Action' },
  ];
  const rows = data;

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Lend Requests Received</h4>
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
                        <TableCell align="center">
                          {row.status}
                        </TableCell>
                        <TableCell align="left">
                          {row.userFirstName + " " + row.userLastName}
                        </TableCell>
                        <TableCell align="left">
                          {row.status === 'PAID' && (
                            <Button size="sm" color="danger" onClick={() => handleClickOpenConfirm(row.lendRequestId)}>Pay Back</Button>
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
          Are you sure you want to payback?
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
    </GridContainer>
  );
}
