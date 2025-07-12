import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import {Layout} from "./components/layout/Layout.tsx";
import {DashboardPage} from "./pages/DashboardPage.tsx";

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          {/*<Route path="/members" element={} />*/}
          {/*<Route path="/accounts" element={} />*/}
          {/*<Route path="/transactions" element={} />*/}
          {/*<Route path="*" element={} />*/}
        </Routes>
      </Layout>
    </Router>
  )
}

export default App
