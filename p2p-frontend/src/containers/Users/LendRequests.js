import React, { useState, useEffect } from "react";
import { Link } from 'react-router-dom';
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import TableScrollbar from 'react-table-scrollbar';
import { makeStyles } from "@material-ui/core/styles";
import { Table, TableHead, TableBody, TableCell, TableRow } from "@material-ui/core";
import FormControl from '@material-ui/core/FormControl';
import InputAdornment from '@material-ui/core/InputAdornment';
import OutlinedInput from '@material-ui/core/OutlinedInput';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import SearchIcon from '@material-ui/icons/Search';
import GridItem from "../../components/Dashboard/Grid/GridItem.js";
import GridContainer from "../../components/Dashboard/Grid/GridContainer.js";
import Card from "../../components/Dashboard/Card/Card.js";
import CardHeader from "../../components/Dashboard/Card/CardHeader.js";
import CardBody from "../../components/Dashboard/Card/CardBody.js";
import Button from "../../components/Dashboard/Button/Button.js";
import styles from "../../components/Dashboard/Styles/DashboardStyles.js";

const useStyles = makeStyles(styles);

export default function LendRequests() {
  const classes = useStyles();
  const [searchTerm, setSearchTerm] = useState("");
  const [openConfirm, setOpenConfirm] = useState(false);
  const [selectedLendRequestId, setSelectedLendRequestId] = useState(null);
  const [data, setData] = useState([]);

  const handleClickOpenConfirm = (lendRequestId) => {
    setSelectedLendRequestId(lendRequestId);
    setOpenConfirm(true);
  };

  const handleCloseConfirm = () => {
    setOpenConfirm(false);
    setSelectedLendRequestId(null);
  };

  const handleConfirm = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/lendrequests/accept`, { lendRequestId: selectedLendRequestId, acceptorId: userId })
      .then((response) => {
        console.log(response);
        fetchData();
      })
      .catch((err) => {
        console.log(err);
      });
    setOpenConfirm(false);
  };

  const fetchData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/lendrequests/exclude`, { userId: userId })
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
    { id: 'requestedBy', label: 'Requested By' },
    { id: 'accept', label: 'Accept' },
  ];

  const rows = data;

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={12}>
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
                    } else if (row.requestedByFirstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                               row.requestedByLastName.toLowerCase().includes(searchTerm.toLowerCase())) {
                      return row;
                    }
                    return null;
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
                          <Link
                            to={`/user/viewuser/${row.requestedUserId}`}
                            style={{ textDecoration: 'underline', color: 'inherit' }}
                          >
                            {row.requestedByFirstName + " " + row.requestedByLastName}
                          </Link>
                        </TableCell>
                        <TableCell align="left">
                          <Button size="sm" color="primary" onClick={() => handleClickOpenConfirm(row.lendRequestId)}>Accept</Button>
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
    </GridContainer>
  );
}
