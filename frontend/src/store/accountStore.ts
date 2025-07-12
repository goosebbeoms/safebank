import type {Account} from "../types/account.ts";
import {create} from "zustand/react";

interface AccountState {
    accounts: Account[];
    selectedAccount: Account | null;
    loading: boolean;
    error: string | null;
    setAccounts: (accounts: Account[]) => void;
    setSelectedAccount: (account: Account | null) => void;
    addAccount: (account: Account) => void;
    setLoading: (loading: boolean) => void;
    setError: (error: string | null) => void;
}

export const useAccountStore = create<AccountState>((set) => ({
    accounts: [],
    selectedAccount: null,
    loading: false,
    error: null,
    setAccounts: (accounts) => set({ accounts }),
    setSelectedAccount: (account) => set({ selectedAccount: account }),
    addAccount: (account) => set((state) => ({ accounts: [...state.accounts, account]})),
    setLoading: (loading) => set({ loading }),
    setError: (error) => set({ error })
}))