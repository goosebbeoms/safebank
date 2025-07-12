import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Card} from "../ui/Card.tsx";
import {Input} from "../ui/Input.tsx";
import {Button} from "../ui/Button.tsx";

const memberSchema = z.object({
    name: z.string().min(1, '이름은 필수입니다').min(2, '이름은 2글자 이상이어야 합니다'),
    email: z.string().email('올바른 이메일 형식이 아닙니다'),
    phoneNumber: z.string().min(1, '전화번호는 필수입니다').regex(/^010-\d{4}-\d{4}$/, '010-0000-0000 형식으로 입력해 주세요')
});

type MemberFormData = z.infer<typeof memberSchema>;

interface MemberFormProps {
    onSubmit: (data: MemberFormData) => void;
    isLoading?: boolean;
}

export const MemberForm = ({onSubmit, isLoading}: MemberFormProps) => {
    const  { register, handleSubmit, formState: { errors }, reset } = useForm<MemberFormData>({
        resolver: zodResolver(memberSchema)
    });

    const handleFormSubmit = (data: MemberFormData) => {
        onSubmit(data);
        reset();
    }

    return (
        <Card title="새 회원 등록">
            <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
                <Input
                    label="이름"
                    {...register('name')}
                    error={errors.name?.message}
                    placeholder="홍길동"
                />
                <Input
                    label="이메일"
                    type="email"
                    {...register('email')}
                    error={errors.email?.message}
                    placeholder="hong@example.com"
                />
                <Input
                    label="전화번호"
                    {...register('phoneNumber')}
                    error={errors.name?.message}
                    placeholder="010-1234-1234"
                />

                <div className="flex justify-end space-x-3">
                    <Button type="button" variant="secondary" onClick={() => reset()}>
                        초기화
                    </Button>
                    <Button type="submit" disabled={isLoading}>
                        {isLoading ? '등록 중...' : '회원 등록'}
                    </Button>
                </div>
            </form>
        </Card>
    )
}

