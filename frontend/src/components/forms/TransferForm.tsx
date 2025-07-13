import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import z from "zod";
import { Card } from "../ui/Card";
import { ArrowRightLeft } from "lucide-react";
import { Input } from "../ui/Input";
import { Button } from "../ui/Button";

const transferSchema = z.object({
    fromAccountNumber: z.string().min(1, '출금 계좌를 선택해주세요'),
    toAccountNumber: z.string().min(1, '입금 계좌를 선택해주세요'),
    amount: z.number().min(1, '이체 금액은 1원 이상이어야 합니다'),
    description: z.string().optional()
})

type TransferFormData = z.infer<typeof transferSchema>

interface TransferFormProps {
    onSubmit: (data: TransferFormData) => void;
    isLoading?: boolean;
    accounts: Array<{ accountNumber: string; ownerName: string; balance: number }>
}

export const TransferForm = ({ onSubmit, isLoading, accounts }: TransferFormProps) => {
    const { register, handleSubmit, formState: { errors }, watch } = useForm<TransferFormData>({
        resolver: zodResolver(transferSchema)
    })

    const fromAccount = watch('fromAccountNumber')
    const selectedAccount = accounts.find(acc => acc.accountNumber === fromAccount)

    return (
        <Card title="계좌 이체">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                    출금 계좌
                    </label>
                    <select
                    {...register("fromAccountNumber")}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="">출금할 계좌를 선택해주세요</option>
                        {accounts.map((account) => (
                            <option
                            key={account.accountNumber}
                            value={account.accountNumber}
                            >
                            {account.accountNumber} - {account.ownerName} (잔액:{" "}
                            {account.balance.toLocaleString()}원)
                            </option>
                        ))}
                    </select>
                    {errors.fromAccountNumber && (
                        <p className="mt-1 text-sm text-red-600">
                            {errors.fromAccountNumber.message}
                        </p>
                    )}
                </div>

                <div className="flex justify-center">
                    <ArrowRightLeft className="h-6 w-6 text-blue-600" />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                    입금 계좌
                    </label>
                    <select
                    {...register("toAccountNumber")}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="">입금받을 계좌를 선택해주세요</option>
                        {accounts.map((account) => (
                            <option
                            key={account.accountNumber}
                            value={account.accountNumber}
                            >
                                {account.accountNumber} - {account.ownerName}
                            </option>
                        ))}
                    </select>
                    {errors.toAccountNumber && (
                        <p className="mt-1 text-sm text-red-600">
                            {errors.toAccountNumber.message}
                        </p>
                    )}
                </div>

                <Input
                    label="이체 금액"
                    type="number"
                    {...register("amount", { valueAsNumber: true })}
                    error={errors.amount?.message}
                    placeholder="50000"
                    min="1"
                    helperText={
                    selectedAccount
                        ? `사용 가능 잔액: ${selectedAccount.balance.toLocaleString()}원`
                        : undefined
                    }
                />

                <Input
                    label="이체 설명 (선택사항)"
                    {...register("description")}
                    placeholder="생활비, 용돈 등"
                />

                <div className="flex justify-end">
                    <Button type="submit" disabled={isLoading}>
                        {isLoading ? "이체 중..." : "이체 실행"}
                    </Button>
                </div>
            </form>
        </Card>
    );
}