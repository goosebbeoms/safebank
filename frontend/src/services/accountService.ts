import type { ApiResponse } from "../types/api.ts";
import type { Account, AccountCreateRequest } from "../types/account.ts";
import api from "./api.ts";

export const accountService = {
  getAll: (): Promise<ApiResponse<Account[]>> =>
    api.get("/accounts").then((res) => res.data),
  getCount: (): Promise<ApiResponse<number>> =>
    api.get("/accounts/count").then((res) => res.data),
  getByNumber: (accountNumber: string): Promise<ApiResponse<Account>> =>
    api.get("/accounts/number/" + accountNumber).then((res) => res.data),
  create: (data: AccountCreateRequest): Promise<ApiResponse<Account>> =>
    api.post("/accounts", data).then((res) => res.data),
  getTotalBalance: (): Promise<ApiResponse<number>> =>
    api.get("/accounts/total-balance").then((res) => res.data)
};
