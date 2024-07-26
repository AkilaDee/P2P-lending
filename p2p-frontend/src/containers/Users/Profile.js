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
import { Table, TableBody, TableCell, TableRow } from "@material-ui/core";

// core components
import GridItem from "../../components/Dashboard/Grid/GridItem.js";
import GridContainer from "../../components/Dashboard/Grid/GridContainer.js";
import Card from "../../components/Dashboard/Card/Card.js";
import CardHeader from "../../components/Dashboard/Card/CardHeader.js";
import CardBody from "../../components/Dashboard/Card/CardBody.js";
import Button from "../../components/Dashboard/Button/Button.js";
import { Rating } from '@material-ui/lab';

import styles from "../../components/Dashboard/Styles/DashboardStyles.js";

const useStyles = makeStyles(styles);

export default function UserProfile() {
  const classes = useStyles();
  const [userData, setUserData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    rating: 0
  });

  const [editOpen, setEditOpen] = useState(false);
  const [editData, setEditData] = useState({
    firstName: '',
    lastName: '',
    email: '',
  });

  const getUserData = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.post(`${backendUrl}/users/profile`, { userId: userId })
      .then(res => {
        const result = res.data;
        setUserData(result);
        setEditData(result); // Initialize edit data with fetched user data
        console.log(res);
      })
      .catch(err => {
        console.log(err);
        setUserData({
          firstName: '',
          lastName: '',
          email: '',
          contactNumber: '',
          rating: 0
        });
      });
  };

  useEffect(() => {
    getUserData();
  }, []);

  const handleEditProfile = () => {
    setEditOpen(true);
  };

  const handleSaveProfile = () => {
    const user = JSON.parse(window.localStorage.getItem('user'));
    const userId = user.userId;
    axios.put(`${backendUrl}/users/profile`, { userId: userId, ...editData })
      .then(res => {
        setUserData(editData);
        setEditOpen(false);
      })
      .catch(err => {
        console.log(err);
      });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEditData({
      ...editData,
      [name]: value
    });
  };

  return (
    <GridContainer>
      <GridItem xs={12} sm={12} md={8}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>User Profile</h4>
          </CardHeader>
          <CardBody>
            <Table>
              <TableBody>
                <TableRow>
                  <TableCell component="th" scope="row">First Name</TableCell>
                  <TableCell>{userData.firstName}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell component="th" scope="row">Last Name</TableCell>
                  <TableCell>{userData.lastName}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell component="th" scope="row">Email</TableCell>
                  <TableCell>{userData.email}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell component="th" scope="row">Average Rating</TableCell>
                  <TableCell>
                    <Rating
                      name="read-only"
                      value={userData.rating}
                      precision={0.5}
                      readOnly
                    />
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardBody>
          <DialogActions>
            <Button color="primary" onClick={handleEditProfile}>
              Edit Profile
            </Button>
          </DialogActions>
        </Card>
      </GridItem>

      <Dialog open={editOpen} onClose={() => setEditOpen(false)}>
        <DialogTitle>Edit Profile</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            label="First Name"
            name="firstName"
            value={editData.firstName}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            margin="dense"
            label="Last Name"
            name="lastName"
            value={editData.lastName}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            margin="dense"
            label="Email"
            name="email"
            value={editData.email}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            margin="dense"
            label="Contact Number"
            name="contactNumber"
            value={editData.contactNumber}
            onChange={handleChange}
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditOpen(false)} color="primary">
            Cancel
          </Button>
          <Button onClick={handleSaveProfile} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </GridContainer>
  );
}
