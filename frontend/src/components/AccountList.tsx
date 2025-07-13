import {Card} from "./ui/Card.tsx";
import {CreditCard, User} from "lucide-react";
import type {Account} from "../types/account.ts";

interface AccountListProps {
    accounts: Account[];
    loading: boolean;
}

export const AccountList = ({ accounts, loading }: AccountListProps) => {
    if (loading) {
        return (
            <Card>
                <div className="flex items-center justify-center py-8">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    <span className="ml-2">로딩 중...</span>
                </div>
            </Card>
        )
    }

    if (accounts.length === 0) {
        return (
            <Card>
                <div className="text-center py-6">
                    <CreditCard className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-500">개설된 계좌가 없습니다.</p>
                </div>
            </Card>
        )
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {accounts.map((account) => (
                <Card key={account.id} className="hover:shadow-lg transition-shadow">
                    <div className="space-y-3">
                        <div className="flex items-center space-x-3">
                            <CreditCard className="h-8 w-8 text-blue-600" />
                            <div>
                                <h3 className="font-semibold text-gray-900">{account.accountNumber}</h3>
                                <span className={`inline-block px-2 py-1 rounded-full text-sx font-medium ${
                                    account.status === 'ACTIVE' ?
                                        'bg-green-100 text-green-800' :
                                        'bg-gray-100 text-gray-800'
                                }`}>
                                    {account.status}
                                </span>
                            </div>
                        </div>

                        <div className="space-y-2">
                            <div className="flex items-center space-x-2 text-sm text-gray-600">
                                <User className="h-4 w-4" />
                                <span>{account.ownerName}</span>
                            </div>
                            <div className="text-lg font-semibold text-blue-600">
                                {account.balance.toLocaleString()}원
                            </div>
                        </div>

                        <div className="text-sm text-gray-500">
                            개설일: {new Date(account.createdAt).toLocaleDateString()}
                        </div>
                    </div>
                </Card>
            ))}
        </div>
    )
}