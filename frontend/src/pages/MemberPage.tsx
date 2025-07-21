import { useMemberStore } from "../store/memberStore.ts";
import { useEffect, useState } from "react";
import { Users } from "lucide-react";
import { Button } from "../components/ui/Button.tsx";
import { MemberForm } from "../components/forms/MemberForm.tsx";
import { MemberList } from "../components/MemberList.tsx";
import { memberService } from "../services/memberService.ts";
import type { MemberCreateRequest } from "../types/member.ts";
import { toast } from "sonner";

export const MemberPage = () => {
  const {
    members,
    loading,
    error,
    setMembers,
    addMember,
    setLoading,
    setError,
  } = useMemberStore();
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    loadMembers();
  }, []);

  const loadMembers = async () => {
    try {
      setLoading(true);
      const response = await memberService.getAll();
      setMembers(response.data);
    } catch (err) {
      toast.error("회원 목록을 불러오는데 실패했습니다.");
      setError("회원 목록을 불러오는데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleCreateMember = async (data: MemberCreateRequest) => {
    try {
      setLoading(true);
      console.log("handleCreateMember", data);
      const response = await memberService.create(data);
      console.log("handleCreateMember response", response);
      addMember(response.data);
      setShowForm(false);
      setError(null);
      toast.success("회원이 생성되었습니다.");
    } catch (err) {
      toast.error("회원 생성에 실패했습니다.");
      setError("회원 생성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div className="flex items-center space-x-3">
          <Users className="h-8 w-8 text-blue-600" />
          <h1 className="text-2xl font-bold text-gray-900">회원 관리</h1>
        </div>
        <Button onClick={() => setShowForm(!showForm)}>
          {showForm ? "목록 보기" : "새 회원 등록"}
        </Button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-600">{error}</p>
        </div>
      )}

      {showForm ? (
        <MemberForm onSubmit={handleCreateMember} isLoading={loading} />
      ) : (
        <MemberList members={members} loading={loading} />
      )}
    </div>
  );
};
