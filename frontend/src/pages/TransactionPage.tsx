/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable @typescript-eslint/no-unused-vars */
import { useEffect, useState } from "react"
import { useAccountStore } from "../store/accountStore"
import { accountService } from "../services/accountService"
import type { TransferRequest } from "../types/transaction"
import { transactionService } from "../services/transactionService"
import { ArrowRightLeft } from "lucide-react"
import { Button } from "../components/ui/Button"
import { TransferForm } from "../components/forms/TransferForm"
import { TransactionList } from "../components/TransactionList"

export const TransactionPage = () => {
    const { accounts, setAccounts } = useAccountStore()
    const [showForm, setShowForm] = useState(false) 
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        loadAccounts()
    }, [])

    const loadAccounts = async () => {
        try {
            const response = await accountService.getAll()
            setAccounts(response.data)
        } catch (err) {
            setError('계좌 목록을 불러오는데 실패했습니다.')
        }
    }

    const handleTransfer = async (data: TransferRequest) => {
        try {
            setLoading(true)
            await transactionService.transfer(data)
            setShowForm(false)
            setError(null)
            await loadAccounts()    // 계좌 목록 재로드(잔액 업데이트)
        } catch (err) {
            setError('이체에 실패했습니다.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div className="flex items-center space-x-3">
                    <ArrowRightLeft className="h-8 w-8 text-purple-600" />
                    <h1 className="text-2xl font-bold text-gray-900">거래 관리</h1>
                </div>
                <Button onClick={() => setShowForm(!showForm)}>
                    {showForm ? '거래 내역 보기' : '계좌 이체'}
                </Button>
            </div>

            {error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                    <p className="text-red-600">{error}</p>
                </div>
            )}

            {showForm ? (
                <TransferForm
                    onSubmit={handleTransfer}
                    isLoading={loading}
                    accounts={accounts}
                />
            ) : (
                <TransactionList />
            )}
        </div>
    )
}