import { useEffect, useState } from "react";
import { Card } from "../components/ui/Card.tsx";
import { ArrowRightLeft, CreditCard, TrendingUp, Users } from "lucide-react";
import { memberService } from "../services/memberService.ts";
import { accountService } from "../services/accountService.ts";
import { transactionService } from "../services/transactionService.ts";

export const DashboardPage = () => {
  const [memberCount, setMemberCount] = useState<number>(0);
  const [accountCount, setAccountCount] = useState<number>(0);
  const [transactionCount, setTransactionCount] = useState<number>(0);
  const [totalBalance, setTotalBalance] = useState<number>(0);

  useEffect(() => {
    loadMemberCount();
    loadAccountCount();
    loadTransactionCount();
    loadTotalBalance();
  }, []);

  const loadMemberCount = async () => {
    const response = await memberService.getCount();
    setMemberCount(response.data);
  };

  const loadAccountCount = async () => {
    const response = await accountService.getCount();
    setAccountCount(response.data);
  };

  const loadTransactionCount = async () => {
    const response = await transactionService.getTransactionCount();
    setTransactionCount(response.data);
  };

  const loadTotalBalance = async () => {
    const response = await accountService.getTotalBalance();
    setTotalBalance(response.data);
  }

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
              <p className="text-2xl font-bold text-gray-900">{accountCount}</p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center">
            <ArrowRightLeft className="h-8 w-8 text-purple-600" />
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">총 거래 수</p>
              <p className="text-2xl font-bold text-gray-900">
                {transactionCount}
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
                {totalBalance}원
              </p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};
