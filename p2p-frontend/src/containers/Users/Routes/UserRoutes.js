import Dashboard from "@material-ui/icons/Dashboard";
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
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: LoanRequests,
    path: "/loanrequests",
    name: "Loan Requests",
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: AddLendRequest,
    path: "/addlendrequest",
    name: "Add Lend Request",
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: AddLoanRequest,
    path: "/addloanrequest",
    name: "Add Loan Request",
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: YourAcceptedLendRequests,
    path: "/YourAcceptedLendRequests",
    name: "Your Accepted Lend Requests",
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: YourAcceptedLoanRequests,
    path: "/YourAcceptedLoanRequests",
    name: "Your Accepted Loan Requests",
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: AcceptedLendRequestsByYou,
    path: "/AcceptedLendRequestsByYou",
    name: "Received Lend Requests",
    icon: Dashboard, 
    layout: "/user",
  },
  {
    component: AcceptedLoanRequestsByYou,
    path: "/AcceptedLoanRequestsByYou",
    name: "Received Loan Requests",
    icon: Dashboard, 
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
    path: "/viewuser",
    name: "View User",
    icon: Dashboard, 
    layout: "/user",
  },
];

export default UserRoutes;
