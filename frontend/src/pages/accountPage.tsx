import { useAccountStore } from "../store/accountStore.ts";
import { useMemberStore } from "../store/memberStore.ts";
import { useEffect, useState } from "react";
import { CreditCard } from "lucide-react";
import { Button } from "../components/ui/Button.tsx";
import { AccountList } from "../components/AccountList.tsx";
import { accountService } from "../services/accountService.ts";
import { AccountForm } from "../components/forms/AccountForm.tsx";
import { memberService } from "../services/memberService.ts";
import type { AccountCreateRequest } from "../types/account.ts";
import { toast } from "sonner";

export const AccountPage = () => {
  const {
    accounts,
    loading,
    error,
    setAccounts,
    addAccount,
    setLoading,
    setError,
  } = useAccountStore();
  const { members, setMembers } = useMemberStore();
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    loadAccounts();
    loadMembers();
  }, []);

  const loadAccounts = async () => {
    try {
      setLoading(true);
      const response = await accountService.getAll();
      setAccounts(response.data);
    } catch (err) {
      toast.error("계좌 목록을 불러오는데 실패했습니다.");
      setError("계좌 목록을 불러오는데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const loadMembers = async () => {
    try {
      const response = await memberService.getAll();
      setMembers(response.data);
    } catch (err) {
      console.error("회원 목록 로드 실패 : ", error);
    }
  };

  const handleCreateAccount = async (data: AccountCreateRequest) => {
    try {
      setLoading(true);
      const response = await accountService.create(data);
      addAccount(response.data);
      setShowForm(false);
      setError(null);
      toast.success("계좌가 생성되었습니다.");
    } catch (err) {
      toast.error("계좌 생성에 실패했습니다.");
      setError("계좌 생성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div className="flex items-center space-x-3">
          <CreditCard className="h-8 w-8 text-greed-600" />
          <h1 className="text-2xl font-bold text-gray-900">계좌 관리</h1>
        </div>
        <Button onClick={() => setShowForm(!showForm)}>
          {showForm ? "목록 보기" : "새 계좌 개설"}
        </Button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-600">{error}</p>
        </div>
      )}

      {showForm ? (
        <AccountForm
          onSubmit={handleCreateAccount}
          isLoading={loading}
          members={members}
        />
      ) : (
        <AccountList accounts={accounts} loading={loading} />
      )}
    </div>
  );
};
