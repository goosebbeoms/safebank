export interface Account {
    id: number;
    accountNumber: string;
    ownerName: string;
    balance: number;
    status: 'ACTIVE' | 'INACTIVE' | 'SUSPANED';
    createdAt: string;
}

export interface AccountCreateRequest {
    memberId: number;
    initialBalance: number;
}