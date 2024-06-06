// @material-ui/icons
import Dashboard from "@material-ui/icons/Dashboard";


import DashboardPage from "../Dashboard.js";

const dashboardRoutes = [
      {
        path: "/dashboard",
        name: "Dashboard",
        icon: Dashboard,
        component: DashboardPage,
        layout: "/user",
      },

      
    ];
    
    export default dashboardRoutes;