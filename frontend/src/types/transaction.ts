export interface Transaction {
    id: number;
    fromAccountNumber: string | null;
    toAccountNumber: string;
    amount: number;
    type: 'TRANSFER' | 'DEPOSIT' | 'WITHDRAWAL'
    status: 'SUCCESS' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
    description: string;
    createdAt: string;
}

export interface TransferRequest {
    fromAccountNumber: string;
    toAccountNumber: string;
    amount: number;
    description?: string;
}