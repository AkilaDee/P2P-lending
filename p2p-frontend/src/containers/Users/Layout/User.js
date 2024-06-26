// import React, { useRef, useEffect, useState } from 'react';
// import { Routes, Route,Router, Navigate } from 'react-router-dom';
// import PerfectScrollbar from 'perfect-scrollbar';
// import 'perfect-scrollbar/css/perfect-scrollbar.css';
// import { makeStyles } from '@material-ui/core/styles';
// import Navbar from '../../../components/Dashboard/Navbar/Navbar';
// import Footer from '../../../components/Dashboard/Footer/Footer';
// import Sidebar from '../../../components/Dashboard/Sidebar/Sidebar';
// import userRoutes from '../Routes/UserRoutes';
// import styles from '../../../components/Dashboard/Styles/AdminStyle';
// import bgImage from '../../../components/Dashboard/Images/two.jpg';
// import logo from '../../../components/Dashboard/Images/one.jpg';
// import LoanRequests from '../LoanRequests';
// import Dashboard from '../Dashboard';
// import LendRequests from '../LendRequests';

// const useStyles = makeStyles(styles);

// let ps;

// export default function User  ({ ...rest }){
//   const classes = useStyles();
//   const mainPanel = useRef(null);
//   const [image, setImage] = useState(bgImage);
//   const [color, setColor] = useState('blue');
//   const [mobileOpen, setMobileOpen] = useState(false);

//   const handleDrawerToggle = () => {
//     setMobileOpen(!mobileOpen);
//   };

//   useEffect(() => {
//     if (navigator.platform.indexOf('Win') > -1) {
//       ps = new PerfectScrollbar(mainPanel.current, {
//         suppressScrollX: true,
//         suppressScrollY: false,
//       });
//       document.body.style.overflow = 'hidden';
//     }
//     return function cleanup() {
//       if (navigator.platform.indexOf('Win') > -1) {
//         ps.destroy();
//       }
//     };
//   }, [mainPanel]);

//   const getRoutes = () =>{
//     userRoutes.map((prop, key) => {
//       if (prop.layout === '/user') {
//         // const Component = prop.component;
//         // setTimeout(() => {
//         //   Component = prop.component;
//         // }, 1000);
//         // console.log("kkkkkkkkkkkkk",prop.layout + prop.path)
//         // {console.log(prop)}
//         return (
//           // <React.Fragment key={prop.layout + prop.path} >
//           <Route
//             path={prop.layout + prop.path}
//             element = {prop.Element}
//             key={key}
//           />
//           // </React.Fragment>
//         );
//       }
//       return null;
//     });
//   }
//   return (
//     <div className={classes.wrapper}>
//       <Sidebar
//         routes={userRoutes}
//         logoText="User"
//         logo={logo}
//         image={image}
//         handleDrawerToggle={handleDrawerToggle}
//         open={mobileOpen}
//         color={color}
//         {...rest}
//       />
//       <div className={classes.mainPanel} ref={mainPanel}>
//         <Navbar routes={userRoutes} handleDrawerToggle={handleDrawerToggle} {...rest} />
//         <div className={classes.content}>
//           <div className={classes.container}>
//             <Routes>
//               {getRoutes()}
//               <Route path="/user" element={<Navigate to="/user/dashboard" replace />} />
//             </Routes>

//           </div>
//         </div>
//         <Footer />
//       </div>
//     </div>
//   );
// };

// // export default User;

import React, { useRef, useEffect, useState } from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import PerfectScrollbar from 'perfect-scrollbar';
import 'perfect-scrollbar/css/perfect-scrollbar.css';
import { makeStyles } from '@material-ui/core/styles';
import Navbar from '../../../components/Dashboard/Navbar/Navbar';
import Footer from '../../../components/Dashboard/Footer/Footer';
import Sidebar from '../../../components/Dashboard/Sidebar/Sidebar';
import routes from '../Routes/UserRoutes';
import styles from '../../../components/Dashboard/Styles/AdminStyle';
import bgImage from '../../../components/Dashboard/Images/two.jpg';
import logo from '../../../components/Dashboard/Images/one.jpg';

const useStyles = makeStyles(styles);

let ps;

export default function User({ ...rest }) {
  const classes = useStyles();
  const mainPanel = useRef(null);
  const location = useLocation();
  const [image, setImage] = useState(bgImage);
  const [color, setColor] = useState('blue');
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  useEffect(() => {
    if (navigator.platform.indexOf('Win') > -1) {
      ps = new PerfectScrollbar(mainPanel.current, {
        suppressScrollX: true,
        suppressScrollY: false,
      });
      document.body.style.overflow = 'hidden';
    }
    return function cleanup() {
      if (navigator.platform.indexOf('Win') > -1) {
        ps.destroy();
      }
    };
  }, [mainPanel]);

  useEffect(() => {
    if (mainPanel.current) {
      mainPanel.current.scrollTop = 0;
    }
  }, [location]);

  const getRoutes = (routes) => {
    return routes.map((prop, key) => {
      if (prop.layout === '/user') {
        return (
          <Route
            path={prop.layout + prop.path}
            element={prop.Element}
            key={key}
          />
        );
      }
      return null;
    });
  };

  return (
    <div className={classes.wrapper}>
      <Sidebar
        routes={routes}
        logoText="User"
        logo={logo}
        image={image}
        handleDrawerToggle={handleDrawerToggle}
        open={mobileOpen}
        color={color}
        {...rest}
      />
      <div className={classes.mainPanel} ref={mainPanel}>
        <Navbar routes={routes} handleDrawerToggle={handleDrawerToggle} {...rest} />
        <div className={classes.content}>
          <div className={classes.container}>
            <Routes>
              {getRoutes(routes)}
              <Route path="/user" element={<Navigate to="/user/dashboard" replace />} />
            </Routes>
          </div>
        </div>
        <Footer />
      </div>
    </div>
  );
}
