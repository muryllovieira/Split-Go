import type { ReactNode } from "react";
import type { MemberBalance } from "../../api/types";
import { Avatar, Money } from "../atoms";

type Props = {
  balance: MemberBalance;
  currency: string;
  expanded: boolean;
  onToggle: () => void;
  children?: ReactNode;
};

export default function BalanceRow({ balance, currency, expanded, onToggle, children }: Props) {
  const status = balance.balance > 0 ? "a receber" : balance.balance < 0 ? "a pagar" : "quite";

  return (
    <li className="py-2">
      <button
        type="button"
        onClick={onToggle}
        aria-expanded={expanded}
        className="flex w-full items-center justify-between gap-3 rounded-lg px-1 py-1 text-left hover:bg-slate-50"
      >
        <span className="flex items-center gap-3">
          <Avatar name={balance.name} />
          <span>
            <span className="block font-medium">{balance.name}</span>
            <span className="block text-xs text-slate-500">
              Pagou <Money value={balance.paid} currency={currency} /> · Consumiu{" "}
              <Money value={balance.consumed} currency={currency} />
            </span>
          </span>
        </span>
        <span className="text-right">
          <Money value={balance.balance} currency={currency} signed className="block font-semibold" />
          <span className="block text-xs text-slate-500">
            {status} {expanded ? "▴" : "▾"}
          </span>
        </span>
      </button>
      {expanded && <div className="ml-11 mt-2">{children}</div>}
    </li>
  );
}
