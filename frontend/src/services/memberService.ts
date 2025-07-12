import type {ApiResponse} from "../types/api.ts";
import type {Member, MemberCreateRequest} from "../types/member.ts";
import api from "./api.ts";
import type {Account} from "../types/account.ts";

export const memberService = {
    getAll: (): Promise<ApiResponse<Member[]>> => (
        api
            .get('/members')
            .then(res => res.data)
    ),
    getById: (id: number): Promise<ApiResponse<Member>> => (
        api
            .get(`/members/${id}`)
            .then(res => res.data)
    ),
    create: (data: MemberCreateRequest): Promise<ApiResponse<Member>> => {
        console.log('start!!')
        return api
            .post('/members', data)
            .then(res => {
                console.log('complete!!!')
                return res.data
            })
            .catch(err => console.error(err))
    },
    getAccount: (id: number): Promise<ApiResponse<Account[]>> => (
        api
            .get(`/members/${id}/accounts`)
            .then(res => res.data)
    )
}