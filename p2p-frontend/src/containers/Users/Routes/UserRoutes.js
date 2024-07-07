import Dashboard from "@material-ui/icons/Dashboard";
import DashboardPage from "../Dashboard.js";
import LendRequests from "../LendRequests.js";
import LoanRequests from "../LoanRequests.js";
import AddLendRequest from "../AddLendRequest.js";
import AddLoanRequest from "../AddLoanRequest.js";

const UserRoutes = [
  {
    component: DashboardPage,
    path: "/dashboard",
    name: "Dashboard",
    icon: Dashboard,
    layout: "/user",
  },
  {
    component: LendRequests,
    path: "/lendrequests",
    name: "Lend Requests",
    icon: Dashboard, // You can use a different icon here if needed
    layout: "/user",
  },
  {
    component: LoanRequests,
    path: "/loanrequests",
    name: "Loan Requests",
    icon: Dashboard, // You can use a different icon here if needed
    layout: "/user",
  },
  {
    component: AddLendRequest,
    path: "/addlendrequest",
    name: "Add Lend Request",
    icon: Dashboard, // You can use a different icon here if needed
    layout: "/user",
  },
  {
    component: AddLoanRequest,
    path: "/addloanrequest",
    name: "Add Loan Request",
    icon: Dashboard, // You can use a different icon here if needed
    layout: "/user",
  },
];

export default UserRoutes;
