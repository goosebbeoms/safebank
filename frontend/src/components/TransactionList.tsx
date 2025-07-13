/* eslint-disable @typescript-eslint/no-unused-vars */
import { useState } from "react"
import type { Transaction } from "../types/transaction";
import { transactionService } from "../services/transactionService";
import { Card } from "./ui/Card";
import { Input } from "./ui/Input";
import { Button } from "./ui/Button";
import { ArrowRightLeft, Search } from "lucide-react";

export const TransactionList = () => {
    const [transactions, setTransactions] = useState<Transaction[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [searchAccount, setSearchAccount] = useState('')

    const handleSearch = async () => {
        if (!searchAccount.trim()) {
            setError('계좌번호를 입력해주세요')
            return
        }

        try {
            setLoading(true)
            setError(null)
            const response = await transactionService.getByAccountNumber(searchAccount.trim())
            setTransactions(response.data.content || [])
        } catch (err) {
            setError('거래 내역을 불러오는데 실패했습니다.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="space-y-6">
            <Card>
                <div className="flex space-x-4">
                    <Input
                        placeholder="계좌번호를 입력하세요 (예: 3333123456789012)"
                        value={searchAccount}
                        onChange={(e) => setSearchAccount(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                    />
                    <Button onClick={handleSearch} disabled={loading}>
                        <Search className="h-4 w-4" />
                    </Button>
                </div>
            </Card>

            {error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                    <p className="text-red-600">{error}</p>
                </div>
            )}

            {loading ? (
                <Card>
                    <div className="flex items-center justify-center py-8">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600"></div>
                        <span className="ml-2">거래 내역 조회 중...</span>
                    </div>
                </Card>
            ) : (
                transactions.length > 0 ? (
                    <Card>
                        <div className="space-y-4">
                            <h3 className="text-lg font-semibold text-gray-900">거래 내역</h3>
                            <div className="space-y-3">
                                {transactions.map((transaction) => (
                                    <div key={transaction.id} className="border-b bordere-gray-200 pb-3 last:border-b-0">
                                        <div className="flex justify-between items-start">
                                            <div className="flex items-center space-x-3">
                                                <ArrowRightLeft className="h-5 w-5 text-purple-600" />
                                                <div>
                                                    <p className="font-medium text-gray-900">
                                                        {transaction.fromAccountNumber || '외부'} → {transaction.toAccountNumber}
                                                    </p>
                                                    <p className="text-sm text-gray-600">{transaction.description}</p>
                                                    <p className="text-xs text-gray-500">
                                                        {new Date(transaction.createdAt).toLocaleDateString()}
                                                    </p>
                                                </div>
                                            </div>
                                            <div className="text-right">
                                                <p className="font-semibold text-lg text-blue-600">
                                                    {transaction.amount.toLocaleString()}원
                                                </p>
                                                <span className={`inline-block px-2 py-2 rounded-full text-sm font-medium ${
                                                    transaction.status === 'COMPLETED'
                                                    ? 'bg-green-100 text-green-800'
                                                    : transaction.status === 'FAILED'
                                                    ? 'bg-red-100 text-red-800'
                                                    : 'bg-yellow-100 text-yellow-800'
                                                }`}>
                                                    {transaction.status}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </Card>
                ) : searchAccount && !loading ? (
                    <Card>
                        <div className="text-center py-8">
                            <ArrowRightLeft className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                            <p className="text-gray-500">해당 계좌의 거래 내역이 없습니다.</p>
                        </div>
                    </Card>
                ) : null
            )}
        </div>
    )
}