import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Card} from "../ui/Card.tsx";
import {Input} from "../ui/Input.tsx";
import {Button} from "../ui/Button.tsx";

const accountSchema = z.object({
    memberId: z.number().min(1, '회원을 선택해 주세요'),
    initialBalance: z.number().min(1000, '초기 잔액은 1,000원 이상이어야 합니다')
})

type AccountFormData = z.infer<typeof accountSchema>;

interface AccountFormProps {
    onSubmit: (data: AccountFormData) => void;
    isLoading?: boolean;
    members: Array<{ id: number, name: string, email: string }>;
}

export const AccountForm = ({ onSubmit, isLoading, members }: AccountFormProps ) => {
    const { register, handleSubmit, formState: { errors }, reset } = useForm<AccountFormData>({
        resolver: zodResolver(accountSchema)
    });

    return (
        <Card title="새 계좌 개설">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        계좌 소유자
                    </label>
                    <select
                        {...register('memberId', {valueAsNumber: true})}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="">회원을 선택해 주세요</option>
                        {members.map(member => (
                            <option key={member.id} value={member.id}>
                                {member.name} ({member.email})
                            </option>
                        ))}
                    </select>
                    {errors.memberId && (
                        <p className="mt-1 text-sm text-red-600">{errors.memberId.message}</p>
                    )}
                </div>

                <Input
                    label="초기 잔액"
                    type="number"
                    {...register('initialBalance', {valueAsNumber: true})}
                    error={errors.initialBalance?.message}
                    placeholder="10000"
                    min="1000"
                    step="100"
                />

                <div className="flex justify-end space-x-3">
                    <Button type="button" variant="secondary" onClick={() => reset()}>
                        초기화
                    </Button>
                    <Button type="submit" disabled={isLoading}>
                        {isLoading ? '개설 중...' : '계좌 개설'}
                    </Button>
                </div>
            </form>
        </Card>
    )
}