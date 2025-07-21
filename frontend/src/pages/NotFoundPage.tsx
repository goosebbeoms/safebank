import { Link } from "react-router-dom";
import { Card } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Home } from "lucide-react";

export const NotFoundPage = () => {
  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <Card className="text-center min-w-md max-w-lg">
        <div className="mb-5">
          <h1 className="text-6xl font-bold text-gray-300 mb-4">404</h1>
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">
            페이지를 찾을 수 없습니다
          </h2>
          <p className="text-gray-600">
            요청하신 페이지가 존재하지 않거나 이동되었습니다.
          </p>
        </div>
        <Link to="/dashboard" className="flex justify-center">
          <Button className="flex items-center space-x-2">
            <Home className="h-4 w-4" />
            <span>대시보드로 돌아가기</span>
          </Button>
        </Link>
      </Card>
    </div>
  );
};
