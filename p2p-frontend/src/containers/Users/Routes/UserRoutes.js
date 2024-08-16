import Dashboard from "@material-ui/icons/Dashboard";
import input from '@mui/icons-material/DriveFileMove';
import output from '@mui/icons-material/ResetTv';
import DashboardPage from "../Dashboard.js";
import LendRequests from "../LendRequests.js";
import LoanRequests from "../LoanRequests.js";
import AddLendRequest from "../AddLendRequest.js";
import AddLoanRequest from "../AddLoanRequest.js";
import YourAcceptedLendRequests from "../YourAcceptedLendRequests.js";
import YourAcceptedLoanRequests from "../YourAcceptedLoanRequests.js";
import AcceptedLendRequestsByYou from "../LendRequestsAcceptedByYou.js"
import AcceptedLoanRequestsByYou from "../LoanRequestsAcceptedByYou.js"
import Profile from "../Profile.js";
import ViewUser from "../ViewUser.js";



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
    icon: input, 
    layout: "/user",
  },
  {
    component: LoanRequests,
    path: "/loanrequests",
    name: "Loan Requests",
    icon: output, 
    layout: "/user",
  },
  {
    component: AddLendRequest,
    path: "/addlendrequest",
    name: "Add Lend Request",
    icon: input, 
    layout: "/user",
  },
  {
    component: AddLoanRequest,
    path: "/addloanrequest",
    name: "Add Loan Request",
    icon: output, 
    layout: "/user",
  },
  {
    component: YourAcceptedLoanRequests,
    path: "/sentloanrequests",
    name: "Sent Loan Requests",
    icon: output, 
    layout: "/user",
  },
  {
    component: AcceptedLoanRequestsByYou,
    path: "/receivedloanrequests",
    name: "Received Loan Requests",
    icon: output, 
    layout: "/user",
  },
  {
    component: YourAcceptedLendRequests,
    path: "/sentlendrequests",
    name: "Sent Lend Requests",
    icon: input, 
    layout: "/user",
  },
  {
    component: AcceptedLendRequestsByYou,
    path: "/receivedlendrequests",
    name: "Received Lend Requests",
    icon: input, 
    layout: "/user",
  },
  {
    component: Profile,
    path: "/profile",
    name: "Profile",
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: ViewUser,
    path: "/viewuser/:userId",
    name: "View User",
    icon: Dashboard, 
    layout: "/user",
  },
];

export default UserRoutes;
