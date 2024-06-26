// routes.js
import Dashboard from "@material-ui/icons/Dashboard";
import DashboardPage from "../Dashboard.js";
import LendRequests from "../LendRequests.js";
import LoanRequests from "../LoanRequests.js";

const UserRoutes = [
  {
    Element: <DashboardPage/>,
    path: "/dashboard",
    name: "Dashboard",
    icon: Dashboard,
    layout: "/user",
  },
  {
    Element: <LendRequests/>,
    path: "/lendrequests",
    name: "Lend Requests",
    icon: Dashboard, // You can use a different icon here if needed
    layout: "/user",
  },
  {
    Element: <LoanRequests/>,
    path: "/loanrequests",
    name: "Loan Requests",
    icon: Dashboard, // You can use a different icon here if needed
    layout: "/user",
  },
];

export default UserRoutes;
