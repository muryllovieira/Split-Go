import { api } from "./client";
import type { CreateExpenseInput, CreatePaymentInput, Expense, Payment, Settlement } from "./types";

export const expensesApi = {
  list: (groupId: string) => api<Expense[]>(`/groups/${groupId}/expenses`),

  create: (groupId: string, input: CreateExpenseInput) =>
    api<Expense>(`/groups/${groupId}/expenses`, { method: "POST", body: JSON.stringify(input) }),
};

export const settlementApi = {
  get: (groupId: string) => api<Settlement>(`/groups/${groupId}/settlement`),

  listPayments: (groupId: string) => api<Payment[]>(`/groups/${groupId}/payments`),

  createPayment: (groupId: string, input: CreatePaymentInput) =>
    api<Payment>(`/groups/${groupId}/payments`, { method: "POST", body: JSON.stringify(input) }),
};
