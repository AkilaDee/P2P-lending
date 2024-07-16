import Dashboard from "@material-ui/icons/Dashboard";
import DashboardPage from "../../Users/Dashboard.js";
import UserRequests from "../UserRequests.js";
import ActiveUsers from "../ActiveUsers.js";



const UserRoutes = [
  {
    component: DashboardPage,
    path: "/dashboard",
    name: "Dashboard",
    icon: Dashboard,
    layout: "/admin",
  },
  {
      component: UserRequests,
      path: "/user/requests",
      name: "User Requests",
      icon: Dashboard,
      layout: "/admin",
  },
  {
    component: ActiveUsers,
    path: "/user/active",
    name: "Active Users",
    icon: Dashboard,
    layout: "/admin",
  }
];

export default UserRoutes;
