/* eslint-disable @typescript-eslint/no-explicit-any */
import type { ApiResponse } from "../types/api";
import type { Transaction, TransferRequest } from "../types/transaction";
import api from "./api";

export const transactionService = {
  transfer: (data: TransferRequest): Promise<ApiResponse<Transaction>> =>
    api.post("/accounts/transfer", data).then((res) => res.data),
  getByAccountNumber: (
    accountNumber: string,
    page = 0,
    size = 20
  ): Promise<ApiResponse<any>> =>
    api
      .get(`/accounts/${accountNumber}/transactions?page=${page}&size=${size}`)
      .then((res) => res.data),
  getTransactionCount: (): Promise<ApiResponse<number>> =>
    api.get("/accounts/transactions/count").then((res) => res.data),
};
