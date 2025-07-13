import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import {Layout} from "./components/layout/Layout.tsx";
import {DashboardPage} from "./pages/DashboardPage.tsx";
import {MemberPage} from "./pages/MemberPage.tsx";
import {AccountPage} from "./pages/accountPage.tsx";
import { TransactionPage } from "./pages/TransactionPage.tsx";

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/members" element={<MemberPage />} />
          <Route path="/accounts" element={<AccountPage />} />
          <Route path="/transactions" element={<TransactionPage />} />
          {/*<Route path="*" element={} />*/}
        </Routes>
      </Layout>
    </Router>
  )
}

export default App
