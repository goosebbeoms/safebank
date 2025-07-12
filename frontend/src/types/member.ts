export interface Member {
    id: number;
    name: string;
    email: string;
    phoneNumber: string;
    status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
    createdAt: string;
}

export interface MemberCreateRequest {
    name: string;
    email: string;
    phoneNumber: string;
}