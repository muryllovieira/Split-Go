import { useState, type FormEvent } from "react";
import type { CreateExpenseInput, Member, SplitType } from "../../api/types";
import { formatMoney, todayIso } from "../../lib/format";
import { Alert, Button, Card, Input, Select } from "../atoms";
import { FormField, ParticipantRow, SplitTypeSelector } from "../molecules";

type ParticipantState = {
  selected: boolean;
  value: string;
};

type Props = {
  members: Member[];
  currency: string;
  onSubmit: (input: CreateExpenseInput) => Promise<unknown>;
  isSubmitting: boolean;
  errorMessage?: string;
};

const roundCents = (value: number) => Math.round(value * 100) / 100;

function initialParticipants(members: Member[]) {
  return Object.fromEntries(members.map((m) => [m.id, { selected: true, value: "" }])) as Record<string, ParticipantState>;
}

export default function ExpenseForm({ members, currency, onSubmit, isSubmitting, errorMessage }: Props) {
  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [payerId, setPayerId] = useState(members[0]?.id ?? "");
  const [expenseDate, setExpenseDate] = useState(todayIso());
  const [splitType, setSplitType] = useState<SplitType>("EQUAL");
  const [participants, setParticipants] = useState(() => initialParticipants(members));

  const amountValue = Number(amount) || 0;
  const selectedIds = members.filter((m) => participants[m.id]?.selected).map((m) => m.id);
  const valuesTotal = roundCents(selectedIds.reduce((sum, id) => sum + (Number(participants[id].value) || 0), 0));

  function updateParticipant(memberId: string, change: Partial<ParticipantState>) {
    setParticipants((current) => ({
      ...current,
      [memberId]: { ...(current[memberId] ?? { selected: true, value: "" }), ...change },
    }));
  }

  function reset() {
    setDescription("");
    setAmount("");
    setSplitType("EQUAL");
    setParticipants(initialParticipants(members));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    try {
      await onSubmit({
        description,
        payerId,
        amount: amountValue,
        expenseDate,
        splitType,
        participants: selectedIds.map((memberId) => ({
          memberId,
          value: splitType === "EQUAL" ? undefined : Number(participants[memberId].value),
        })),
      });
      reset();
    } catch {
      // O erro chega pela prop errorMessage; os dados ficam no formulário para correção
    }
  }

  return (
    <Card>
      <h2 className="text-lg font-semibold">Nova despesa</h2>
      <form onSubmit={handleSubmit} className="mt-4 space-y-5">
        <div className="grid gap-4 sm:grid-cols-[1fr_10rem]">
          <FormField label="Descrição">
            <Input
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Ex: Jantar no restaurante"
              maxLength={140}
            />
          </FormField>
          <FormField label={`Valor (${currency})`}>
            <Input
              type="number"
              inputMode="decimal"
              step="0.01"
              min="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="0,00"
            />
          </FormField>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <FormField label="Quem pagou?">
            <Select
              value={payerId}
              onChange={(e) => setPayerId(e.target.value)}
              options={members.map((m) => ({ value: m.id, label: m.name }))}
            />
          </FormField>
          <FormField label="Data">
            <Input type="date" value={expenseDate} onChange={(e) => setExpenseDate(e.target.value)} />
          </FormField>
        </div>

        <div>
          <span className="text-sm font-medium text-slate-700">Como dividir?</span>
          <div className="mt-1">
            <SplitTypeSelector value={splitType} onChange={setSplitType} />
          </div>
        </div>

        <div>
          <span className="text-sm font-medium text-slate-700">Entre quem?</span>
          <div className="mt-1 divide-y divide-slate-100">
            {members.map((member) => (
              <ParticipantRow
                key={member.id}
                name={member.name}
                selected={participants[member.id]?.selected ?? false}
                onToggle={(selected) => updateParticipant(member.id, { selected })}
                value={participants[member.id]?.value ?? ""}
                onValueChange={(value) => updateParticipant(member.id, { value })}
                showValue={splitType !== "EQUAL"}
                valueSuffix={splitType === "PERCENTAGE" ? "%" : undefined}
              />
            ))}
          </div>
          <SplitSummary
            splitType={splitType}
            amount={amountValue}
            selectedCount={selectedIds.length}
            valuesTotal={valuesTotal}
            currency={currency}
          />
        </div>

        {errorMessage && <Alert message={errorMessage} />}

        <Button type="submit" fullWidth disabled={isSubmitting || members.length === 0} className="py-2.5">
          {isSubmitting ? "Salvando..." : "Lançar despesa"}
        </Button>
      </form>
    </Card>
  );
}

type SummaryProps = {
  splitType: SplitType;
  amount: number;
  selectedCount: number;
  valuesTotal: number;
  currency: string;
};

function SplitSummary({ splitType, amount, selectedCount, valuesTotal, currency }: SummaryProps) {
  if (selectedCount === 0) {
    return <p className="mt-2 text-sm text-amber-700">Selecione pelo menos uma pessoa.</p>;
  }

  if (splitType === "EQUAL") {
    return (
      <p className="mt-2 text-sm text-slate-500">
        ≈ {formatMoney(amount / selectedCount, currency)} por pessoa
      </p>
    );
  }

  const target = splitType === "PERCENTAGE" ? 100 : amount;
  const remaining = roundCents(target - valuesTotal);
  const format = (value: number) => (splitType === "PERCENTAGE" ? `${value}%` : formatMoney(value, currency));
  const ok = remaining === 0;

  return (
    <p className={`mt-2 text-sm ${ok ? "text-emerald-700" : "text-amber-700"}`}>
      Total: {format(valuesTotal)} de {format(target)}
      {ok ? " ✓" : remaining > 0 ? ` · faltam ${format(remaining)}` : ` · passou ${format(-remaining)}`}
    </p>
  );
}
