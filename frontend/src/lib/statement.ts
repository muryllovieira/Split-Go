import type { Expense, Payment } from "../api/types";

export type StatementEntry = {
  key: string;
  label: string;
  amount: number;
};

// Cada lançamento afeta o saldo com o mesmo sinal da fórmula do backend:
// saldo = pagou − consumiu + enviou − recebeu
export function buildStatement(memberId: string, expenses: Expense[], payments: Payment[]): StatementEntry[] {
  const entries: StatementEntry[] = [];

  for (const expense of expenses) {
    if (expense.payer.id === memberId) {
      entries.push({ key: `paid-${expense.id}`, label: `Pagou "${expense.description}"`, amount: expense.amount });
    }
    const split = expense.splits.find((s) => s.member.id === memberId);
    if (split) {
      entries.push({ key: `share-${expense.id}`, label: `Sua parte em "${expense.description}"`, amount: -split.amount });
    }
  }

  for (const payment of payments) {
    if (payment.from.id === memberId) {
      entries.push({ key: `sent-${payment.id}`, label: `Pagou ${payment.to.name}`, amount: payment.amount });
    }
    if (payment.to.id === memberId) {
      entries.push({ key: `received-${payment.id}`, label: `Recebeu de ${payment.from.name}`, amount: -payment.amount });
    }
  }

  return entries;
}
