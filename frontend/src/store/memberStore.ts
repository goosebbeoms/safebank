import type {Member} from "../types/member.ts";
import {create} from "zustand/react";

interface MemberState {
    members: Member[];
    selectedMember: Member | null;
    loading: boolean;
    error: string | null;
    setMembers: (members: Member[]) => void;
    setSelectedMember: (member: Member | null) => void;
    addMember: (member: Member) => void;
    setLoading: (loading: boolean) => void;
    setError: (error: string | null) => void;
}

export const useMemberStore = create<MemberState>((set) => ({
    members: [],
    selectedMember: null,
    loading: false,
    error: null,
    setMembers: (members) => set({ members }),
    setSelectedMember: (member) => set({ selectedMember: member }),
    addMember: (member) => set((state) => ({ members: [...state.members, member]})),
    setLoading: (loading) => set({ loading }),
    setError: (error) => set({ error })
}))