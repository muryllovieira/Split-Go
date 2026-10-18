import type { Expense } from "../../api/types";
import { formatDate, formatMoney } from "../../lib/format";
import { Badge, Money } from "../atoms";

const SPLIT_LABELS = {
  EQUAL: "Igual",
  PERCENTAGE: "Porcentagem",
  EXACT: "Valor fixo",
} as const;

type Props = {
  expense: Expense;
  currency: string;
};

export default function ExpenseItem({ expense, currency }: Props) {
  return (
    <li className="py-3">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="font-medium">{expense.description}</p>
          <p className="text-sm text-slate-500">
            {expense.payer.name} pagou · {formatDate(expense.expenseDate)}
          </p>
        </div>
        <div className="text-right">
          <Money value={expense.amount} currency={currency} className="font-semibold" />
          <div className="mt-1">
            <Badge>{SPLIT_LABELS[expense.splitType]}</Badge>
          </div>
        </div>
      </div>
      <p className="mt-1 text-xs text-slate-500">
        {expense.splits
          .map((split) =>
            `${split.member.name} ${formatMoney(split.amount, currency)}` +
            (split.percentage !== null ? ` (${split.percentage}%)` : ""),
          )
          .join(" · ")}
      </p>
    </li>
  );
}
