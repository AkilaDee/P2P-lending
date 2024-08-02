import React, { useState, useEffect } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import { useHistory } from 'react-router-dom';

// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import { Table, TableBody, TableCell, TableRow } from "@material-ui/core";
import { Rating } from '@material-ui/lab';

// core components
import GridItem from "../../components/Dashboard/Grid/GridItem.js";
import GridContainer from "../../components/Dashboard/Grid/GridContainer.js";
import Card from "../../components/Dashboard/Card/Card.js";
import CardHeader from "../../components/Dashboard/Card/CardHeader.js";
import CardBody from "../../components/Dashboard/Card/CardBody.js";
import Button from "../../components/Dashboard/Button/Button.js";

import styles from "../../components/Dashboard/Styles/DashboardStyles.js";

const useStyles = makeStyles(styles);

export default function ViewUser() {
  const classes = useStyles();
  const history = useHistory();
  const [userData, setUserData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    rating: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem('selectedUser');
    if (storedUser) {
      const user = JSON.parse(storedUser);
      getUserData(user.userId);
    } else {
      history.push('/');
    }
  }, [history]);

  const getUserData = (userId) => {
    axios.post(`${backendUrl}/users/viewuser`, { userId })
      .then(res => {
        setUserData(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.log(err);
        setError('Failed to fetch user data.');
        setLoading(false);
      });
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

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
        </Card>
      </GridItem>
    </GridContainer>
  );
}
