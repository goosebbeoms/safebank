import { useEffect, useState } from "react";
import { Card } from "../components/ui/Card.tsx";
import { ArrowRightLeft, CreditCard, TrendingUp, Users } from "lucide-react";
import { memberService } from "../services/memberService.ts";

export const DashboardPage = () => {
  const [memberCount, setMemberCount] = useState<number>(0);

  useEffect(() => {
    loadMemberCount();
  }, []);

  const loadMemberCount = async () => {
    const response = await memberService.getCount();
    setMemberCount(response.data);
  };

  // sample data
  const stats = {
    totalMembers: 150,
    totalAccounts: 1000,
    totalTransactions: 10000,
    totalBalance: 12500000,
  };

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">대시보드</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <div className="flex items-center">
            <Users className="h-8 w-8 text-blue-600" />
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">총 회원 수</p>
              <p className="text-2xl font-bold text-gray-900">{memberCount}</p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center">
            <CreditCard className="h-8 w-8 text-green-600" />
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">총 계좌 수</p>
              <p className="text-2xl font-bold text-gray-900">
                {stats.totalAccounts.toLocaleString()}
              </p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center">
            <ArrowRightLeft className="h-8 w-8 text-purple-600" />
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">총 거래 수</p>
              <p className="text-2xl font-bold text-gray-900">
                {stats.totalTransactions.toLocaleString()}
              </p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center">
            <TrendingUp className="h-8 w-8 text-yellow-600" />
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">총 자산</p>
              <p className="text-2xl font-bold text-gray-900">
                {stats.totalBalance.toLocaleString()}원
              </p>
            </div>
          </div>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="최근 거래 내역">
          <div className="space-y-3">
            {/* 실제로는 API에서 최근 거래 데이터를 가져올 것 */}
            <div className="flex justify-between items-center py-2 border-b">
              <div>
                <p className="font-medium">홍길동 → 김철수</p>
                <p className="text-sm text-gray-600">생활비</p>
              </div>
              <div className="text-right">
                <p className="font-medium text-red-600">-50,000원</p>
                <p className="text-sm text-gray-600">2분 전</p>
              </div>
            </div>
            {/* 더 많은 거래 내역... */}
          </div>
        </Card>

        <Card title="계좌별 잔액 현황">
          <div className="space-y-3">
            {/* 실제로는 API에서 계좌 데이터를 가져올 것 */}
            <div className="flex justify-between items-center py-2 border-b">
              <div>
                <p className="font-medium">3333-1234-5678-9012</p>
                <p className="text-sm text-gray-600">홍길동</p>
              </div>
              <div className="text-right">
                <p className="font-medium text-blue-600">1,500,000원</p>
              </div>
            </div>
            {/* 더 많은 계좌 정보... */}
          </div>
        </Card>
      </div>
    </div>
  );
};
