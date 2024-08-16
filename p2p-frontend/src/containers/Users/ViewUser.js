import React, { useState, useEffect } from "react";
import axios from 'axios';
import { backendUrl } from '../../UrlConfig.js';
import { useHistory, useParams } from 'react-router-dom';

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

import styles from "../../components/Dashboard/Styles/DashboardStyles.js";

const useStyles = makeStyles(styles);

export default function ViewUser() {
  const classes = useStyles();
  const [data, setData] = useState({
    firstName: '',
    lastName: '',
    // email: '',
    rating: 0 // Initialize with a default rating value
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const history = useHistory();
  const { userId } = useParams(); 

  const fetchData = async () => {
    try {
      const response = await axios.post(`${backendUrl}/users/viewuser`, { userId });
      setData(response.data);
      setLoading(false);
    } catch (err) {
      console.error("Error fetching data:", err);
      setError('Failed to fetch user data.');
      setLoading(false);
    }
  };

  useEffect(() => {
    if (userId) {
      fetchData();
    } else {
      history.push('/');
    }
  }, [userId, history]);

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
                  <TableCell>{data.firstName}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell component="th" scope="row">Last Name</TableCell>
                  <TableCell>{data.lastName}</TableCell>
                </TableRow>
                {/* <TableRow>
                  <TableCell component="th" scope="row">Email</TableCell>
                  <TableCell>{data.email}</TableCell>
                </TableRow> */}
                <TableRow>
                  <TableCell component="th" scope="row">Average Rating</TableCell>
                  <TableCell>
                    <Rating
                      name="read-only"
                      value={data.rating}
                      precision={0.5}
                      readOnly
                    />
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell component="th" scope="row">Number of rating</TableCell>
                  <TableCell>{data.ratingCount}</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardBody>
        </Card>
      </GridItem>
    </GridContainer>
  );
}
