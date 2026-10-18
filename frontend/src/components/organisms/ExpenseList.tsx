import type { Expense } from "../../api/types";
import { Card, Money } from "../atoms";
import { ExpenseItem } from "../molecules";

type Props = {
  expenses: Expense[] | undefined;
  currency: string;
  isLoading: boolean;
};

export default function ExpenseList({ expenses, currency, isLoading }: Props) {
  const total = expenses?.reduce((sum, e) => sum + e.amount, 0) ?? 0;

  return (
    <Card>
      <div className="flex items-baseline justify-between">
        <h2 className="text-lg font-semibold">Despesas</h2>
        {expenses && expenses.length > 0 && (
          <span className="text-sm text-slate-500">
            Total: <Money value={total} currency={currency} className="font-semibold text-slate-900" />
          </span>
        )}
      </div>

      {isLoading && <p className="mt-3 text-sm text-slate-500">Carregando...</p>}

      {expenses && expenses.length === 0 && (
        <p className="mt-3 text-sm text-slate-500">Nenhuma despesa ainda. Lance a primeira acima!</p>
      )}

      {expenses && expenses.length > 0 && (
        <ul className="mt-2 divide-y divide-slate-100">
          {expenses.map((expense) => (
            <ExpenseItem key={expense.id} expense={expense} currency={currency} />
          ))}
        </ul>
      )}
    </Card>
  );
}
