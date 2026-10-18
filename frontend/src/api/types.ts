export type Group = {
  id: string;
  name: string;
  baseCurrency: string;
  createdAt: string;
};

export type Member = {
  id: string;
  name: string;
  createdAt: string;
};

export type CreateGroupInput = {
  name: string;
  baseCurrency: string;
  members: string[];
};

export type MemberSummary = {
  id: string;
  name: string;
};

export type SplitType = "EQUAL" | "PERCENTAGE" | "EXACT";

export type ExpenseSplit = {
  member: MemberSummary;
  amount: number;
  percentage: number | null;
};

export type Expense = {
  id: string;
  description: string;
  payer: MemberSummary;
  amount: number;
  originalAmount: number;
  originalCurrency: string;
  exchangeRate: number;
  splitType: SplitType;
  expenseDate: string;
  createdAt: string;
  splits: ExpenseSplit[];
};

export type CreateExpenseInput = {
  description: string;
  payerId: string;
  amount: number;
  expenseDate: string;
  splitType: SplitType;
  participants: { memberId: string; value?: number }[];
};

export type MemberBalance = {
  id: string;
  name: string;
  paid: number;
  consumed: number;
  sent: number;
  received: number;
  balance: number;
};

export type Transfer = {
  from: MemberSummary;
  to: MemberSummary;
  amount: number;
};

export type Settlement = {
  currency: string;
  settled: boolean;
  members: MemberBalance[];
  transfers: Transfer[];
};

export type Payment = {
  id: string;
  from: MemberSummary;
  to: MemberSummary;
  amount: number;
  createdAt: string;
};

export type CreatePaymentInput = {
  fromMemberId: string;
  toMemberId: string;
  amount: number;
};
