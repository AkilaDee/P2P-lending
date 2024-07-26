import React, { useEffect, useState, useRef } from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';
import PerfectScrollbar from 'react-perfect-scrollbar';
import 'react-perfect-scrollbar/dist/css/styles.css';
import { makeStyles } from '@material-ui/core/styles';
import Navbar from '../../../components/Dashboard/Navbar/Navbar';
import Footer from '../../../components/Dashboard/Footer/Footer';
import Sidebar from '../../../components/Dashboard/Sidebar/Sidebar';
import routes from '../Routes/UserRoutes';
import styles from '../../../components/Dashboard/Styles/AdminStyle';
import bgImage from '../../../components/Dashboard/Images/two.jpg';
// import logo from '../../../components/Dashboard/Images/one.jpg';
import logo from '../../../components/Dashboard/Images/pf3.jpg';

const useStyles = makeStyles(styles);

const switchRoutes = (
  <Switch>
    {routes.map((prop, key) => {
      if (prop.layout === "/user") {
        return (
          <Route
            path={prop.layout + prop.path}
            component={prop.component}
            key={key}
          />
        );
      }
      return null;
    })}
    <Redirect from="/user" to="/user/dashboard" />
  </Switch>
);

export default function User({ ...rest }) {
  const classes = useStyles();
  const mainPanel = useRef(null);
  const [image, setImage] = useState(bgImage);
  const [color, setColor] = useState('blue');
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  useEffect(() => {
    const resizeFunction = () => {
      if (window.innerWidth >= 960) {
        setMobileOpen(false);
      }
    };

    window.addEventListener('resize', resizeFunction);

    return () => {
      window.removeEventListener('resize', resizeFunction);
    };
  }, []);

  //Filter out the profile route for the sidebar
  const sidebarRoutes = routes.filter(route => route.path !== '/profile');

  return (
    <div className={classes.wrapper}>
      <Sidebar
        routes={sidebarRoutes}
        logoText="PeerFund"
        logo={logo}
        image={image}
        handleDrawerToggle={handleDrawerToggle}
        open={mobileOpen}
        color={color}
        {...rest}
      />
      <div className={classes.mainPanel} ref={mainPanel}>
        <Navbar routes={routes} handleDrawerToggle={handleDrawerToggle} {...rest} />
        <PerfectScrollbar>
          <div className={classes.content}>
            <div className={classes.container}>
              {switchRoutes}
            </div>
          </div>
        </PerfectScrollbar>
        <Footer />
      </div>
    </div>
  );
}
