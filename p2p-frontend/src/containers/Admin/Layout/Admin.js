import React, { useEffect, useState } from 'react';
import { Route, Redirect, Switch, useLocation } from 'react-router-dom';
import PerfectScrollbar from 'react-perfect-scrollbar';
import 'react-perfect-scrollbar/dist/css/styles.css';
import { makeStyles } from '@material-ui/core/styles';
import Navbar from '../../../components/Dashboard/Navbar/Navbar';
import Footer from '../../../components/Dashboard/Footer/Footer';
import Sidebar from '../../../components/Dashboard/Sidebar/Sidebar';
import routes from '../Routes/AdminRoutes';
import styles from '../../../components/Dashboard/Styles/AdminStyle';
import bgImage from '../../../components/Dashboard/Images/two.jpg';
import logo from '../../../components/Dashboard/Images/pf3.jpg';

const useStyles = makeStyles(styles);

const switchRoutes = (
  <Switch>
    {routes.map((prop, key) => {
      if (prop.layout === "/admin") {
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
    <Redirect from="/admin" to="/admin/dashboard" />
  </Switch>
);

export default function Admin({ ...rest }) {
  const classes = useStyles();
  const mainPanel = React.createRef();
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

  return (
    <div className={classes.wrapper}>
      <Sidebar
        routes={routes}
        logoText="Admin"
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
              {/* <Switch>
                {switchRoutes(routes)}
                <Route path="/Admin" render={() => <Redirect to="/Admin/dashboard" />} />
              </Switch> */}
            </div>
          </div>
        </PerfectScrollbar>
        <Footer />
      </div>
    </div>
  );
}
