import type {Member} from "../types/member.ts";
import {Card} from "./ui/Card.tsx";
import {Mail, Phone, User} from "lucide-react";

interface MemberListProps {
    members: Member[];
    loading: boolean;
}

export const MemberList = ({ members, loading }: MemberListProps) => {
    if (loading) {
        return (
            <Card>
                <div className="flex items-center justify-center py-8">
                    <div className="animate-spin rouned-full h-8 w-8 border-b-2 border-blue-600"></div>
                    <span className="ml-2">로딩 중...</span>
                </div>
            </Card>
        )
    }

    if (members.length === 0) {
        return (
            <Card>
                <div className="text-center py-8">
                    <User className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-500">등록된 회원이 없습니다.</p>
                </div>
            </Card>
        )
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {members.map((member) => (
                <Card key={member.id} className="hover:shadow-lg transition-shadow">
                    <div className="space-y-3">
                        <div className="flex items-center space-x-3">
                            <User className="h-8 w-8 text-blue-600" />
                            <div>
                                <h3 className="font-semibold text-gray-900">{member.name}</h3>
                                <span className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${
                                    member.status === 'ACTIVE'
                                        ? 'bg-green-100 text-green-800'
                                        : 'bg-gray-100 text-gray-800'
                                }`}>
                  {member.status}
                </span>
                            </div>
                        </div>

                        <div className="space-y-2 text-sm text-gray-600">
                            <div className="flex items-center space-x-2">
                                <Mail className="h-4 w-4" />
                                <span>{member.email}</span>
                            </div>
                            <div className="flex items-center space-x-2">
                                <Phone className="h-4 w-4" />
                                <span>{member.phoneNumber}</span>
                            </div>
                        </div>

                        <div className="text-xs text-gray-500">
                            가입일: {new Date(member.createdAt).toLocaleDateString()}
                        </div>
                    </div>
                </Card>
            ))}
        </div>
    );
}