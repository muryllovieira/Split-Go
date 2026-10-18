import { useState } from "react";
import type { Expense, Payment, Settlement, Transfer } from "../../api/types";
import { buildStatement } from "../../lib/statement";
import { Alert, Card, Money } from "../atoms";
import { BalanceRow, TransferItem } from "../molecules";

type Props = {
  settlement: Settlement;
  expenses: Expense[];
  payments: Payment[];
  onMarkAsPaid: (transfer: Transfer) => void;
  isPaying: boolean;
  errorMessage?: string;
};

export default function SettlementPanel({ settlement, expenses, payments, onMarkAsPaid, isPaying, errorMessage }: Props) {
  const [expandedId, setExpandedId] = useState<string | null>(null);
  const { currency } = settlement;

  return (
    <div className="space-y-6">
      <Card>
        <h2 className="text-lg font-semibold">Plano de quitação</h2>
        {settlement.settled ? (
          <p className="mt-3 rounded-lg bg-emerald-50 px-4 py-3 text-emerald-800">
            🎉 Tudo certo! Ninguém deve nada a ninguém.
          </p>
        ) : (
          <>
            <p className="mt-1 text-sm text-slate-500">
              {settlement.transfers.length === 1
                ? "Só 1 transferência quita o grupo inteiro."
                : `${settlement.transfers.length} transferências quitam o grupo inteiro — o mínimo possível.`}
            </p>
            <ul className="mt-2 divide-y divide-slate-100">
              {settlement.transfers.map((transfer) => (
                <TransferItem
                  key={`${transfer.from.id}-${transfer.to.id}`}
                  transfer={transfer}
                  currency={currency}
                  onMarkAsPaid={() => onMarkAsPaid(transfer)}
                  disabled={isPaying}
                />
              ))}
            </ul>
          </>
        )}
        {errorMessage && (
          <div className="mt-3">
            <Alert message={errorMessage} />
          </div>
        )}
      </Card>

      <Card>
        <h2 className="text-lg font-semibold">Extrato por pessoa</h2>
        <p className="mt-1 text-sm text-slate-500">Toque em alguém para ver os lançamentos.</p>
        <ul className="mt-2 divide-y divide-slate-100">
          {settlement.members.map((member) => {
            const entries = buildStatement(member.id, expenses, payments);
            return (
              <BalanceRow
                key={member.id}
                balance={member}
                currency={currency}
                expanded={expandedId === member.id}
                onToggle={() => setExpandedId(expandedId === member.id ? null : member.id)}
              >
                {entries.length === 0 ? (
                  <p className="text-sm text-slate-500">Nenhum lançamento.</p>
                ) : (
                  <ul className="space-y-1 text-sm">
                    {entries.map((entry) => (
                      <li key={entry.key} className="flex justify-between gap-4">
                        <span className="text-slate-600">{entry.label}</span>
                        <Money value={entry.amount} currency={currency} signed />
                      </li>
                    ))}
                    <li className="flex justify-between gap-4 border-t border-slate-100 pt-1 font-semibold">
                      <span>Saldo</span>
                      <Money value={member.balance} currency={currency} signed />
                    </li>
                  </ul>
                )}
              </BalanceRow>
            );
          })}
        </ul>
      </Card>
    </div>
  );
}
